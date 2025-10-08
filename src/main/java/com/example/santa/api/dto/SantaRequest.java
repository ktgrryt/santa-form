package com.example.santa.api.dto;

public class SantaRequest {
    public String name;       // 名前
    public String age;        // 年齢（文字列で送る想定）
    public String present;    // 欲しいプレゼント
    public String address;    // 住所
    public String region;     // お住まいの地域（Japan, US, APAC, EMEA）
    public String msg;        // サンタさんへのメッセージ

    // デフォルトコンストラクタ/ゲッター/セッターはJSON-Bで省略可（publicフィールド）
}