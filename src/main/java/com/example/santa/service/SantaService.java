package com.example.santa.service;

import com.example.santa.api.dto.SantaRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 地域に応じたエンドポイント/キーで外部APIをコール。
 * - microprofile-config.properties から
 *   santa.api.<REGION>.url
 *   santa.api.<REGION>.apiKey
 *   を読み取る（REGIONは Japan / US / APAC / EMEA）
 * - 例外があってもフロントには成功を返す（画面は「送信されました」を表示）
 * - PIIはログに出さない/マスクする
 */
@ApplicationScoped
public class SantaService {
    private static final Logger LOG = Logger.getLogger(SantaService.class.getName());
    private final Config config = ConfigProvider.getConfig();

    public void sendToGateway(SantaRequest req) {
        final String region = normalizeRegion(req.region);
        final Optional<String> urlOpt = config.getOptionalValue("santa.api." + region + ".url", String.class);
        final Optional<String> keyOpt = config.getOptionalValue("santa.api." + region + ".apiKey", String.class);

        if (urlOpt.isEmpty() || keyOpt.isEmpty()) {
            LOG.warning(() -> "[SantaService] 設定未定義: region=" + region + " / url or apiKey is missing");
            return; // 設定がなければ何もせず終了（フロントは成功表示方針）
        }

        String url = urlOpt.get();
        String apiKey = keyOpt.get();

        // 送出するJSON：提供されたサンプルに合わせてフィールド名を維持
        // （present は任意：API側が無視してもOK）
        var payload = new Payload(req);

        try (Client client = ClientBuilder.newClient(); Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(payload);

            Response res = client
                    .target(URI.create(url))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("x-Gateway-APIKey", apiKey)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));

            // レスポンスは無視。ただし状態のみログ（詳細本文は記録しない）
            int status = res.getStatus();
            LOG.info(() -> "[SantaService] API呼出完了: region=" + region + " status=" + status);
            res.close();
        } catch (Exception e) {
            // 失敗しても画面は成功表示にする要件のため、ここではログのみ
            LOG.log(Level.WARNING, "[SantaService] API呼出失敗: region=" + region + " reason=" + e.getMessage(), e);
        }
    }

    private String normalizeRegion(String region) {
        if (region == null) return "APAC";
        String r = region.trim().toUpperCase();
        return switch (r) {
            case "JAPAN" -> "Japan";
            case "US" -> "US";
            case "EMEA" -> "EMEA";
            default -> "APAC";
        };
    }

    /**
     * 外部APIへ送るDTO（サンプルcurlに合わせたキー）
     */
    public static class Payload {
        public String name;
        public String age;
        public String address;
        public String region;
        public String msg;
        public String present; // 任意

        public Payload() {}

        public Payload(SantaRequest req) {
            this.name = req.name;
            this.age = req.age;
            this.address = req.address;
            this.region = req.region;
            this.msg = req.msg;
            this.present = req.present;
        }
    }

    // PIIをログに出さない & 住所などをマスクしたい場合に拡張可能
    @SuppressWarnings("unused")
    private String mask(String s) {
        if (s == null || s.isBlank()) return s;
        int n = Math.min(6, s.length());
        return s.substring(0, s.length() - n) + "*".repeat(n);
    }
}