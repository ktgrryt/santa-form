package com.example.santa.api;

import com.example.santa.api.dto.SantaRequest;
import com.example.santa.service.SantaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * フロントエンドからの送信受付用エンドポイント。
 * 要件により、外部APIのレスポンスは無視し、常に「送信されました」を返す。
 */
@Path("/submit")
public class SantaResource {

    @Inject
    SantaService santaService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(SantaRequest req) {
        // 入力の軽微なサーバサイド検証（null/空チェックなど）※必要に応じて拡張
        if (req == null || isBlank(req.name) || isBlank(req.age) ||
            isBlank(req.address) || isBlank(req.region)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"必須項目が不足しています。\"}")
                    .build();
        }

        // 外部API呼出（失敗しても画面は成功ダイアログを出す）
        santaService.sendToGateway(req);

        // レスポンスは固定（フロントはこれを見てダイアログ表示）
        return Response.accepted()
                .entity("{\"message\":\"送信されました\"}")
                .build();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}