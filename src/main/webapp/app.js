(() => {
  const form = document.getElementById('santaForm');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const data = {
      name: document.getElementById('name').value.trim(),
      age: document.getElementById('age').value.trim(), // サンプルcurl準拠で文字列化
      present: document.getElementById('present').value.trim(),
      address: document.getElementById('address').value.trim(),
      region: document.getElementById('region').value,
      msg: document.getElementById('msg').value.trim(),
    };

    // 簡易クライアントサイド検証
    if (!data.name || !data.age || !data.address || !data.region) {
      alert('必須項目が不足しています。');
      return;
    }

    const t0 = performance.now();
    try {
      const res = await fetch('api/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=UTF-8' },
        body: JSON.stringify(data),
      });

      // ▼ ステータスコードから成功/失敗を標準出力（コンソール）へ
      const { status, statusText } = res;
      if (res.ok) {
        console.log(
          `[SUCCESS] POST api/submit -> ${status} ${statusText || ''}`.trim()
        );
      } else {
        let bodyText = '';
        try {
          const ct = res.headers.get('content-type') || '';
          if (ct.includes('application/json')) {
            const j = await res.json();
            bodyText = ` | body: ${JSON.stringify(j)}`;
          } else {
            bodyText = ` | body: ${await res.text()}`;
          }
        } catch (_) {
          // ボディ取得に失敗しても無視
        }
        console.error(
          `[FAILURE] POST api/submit -> ${status} ${statusText || ''}${bodyText}`.trim()
        );
      }

      // 要件：APIの応答は画面上では無視。常に「送信されました」表示
      alert('送信されました');
      form.reset();
      document.getElementById('name').focus();
    } catch (err) {
      // ネットワークエラー（CORS/オフライン/タイムアウト等）
      console.error('[NETWORK ERROR] POST api/submit failed:', err);

      // 要件どおり表示は統一
      alert('送信されました');
      form.reset();
    } finally {
      const t1 = performance.now();
      console.log(`[INFO] api/submit request time: ${(t1 - t0).toFixed(1)} ms`);
    }
  });
})();