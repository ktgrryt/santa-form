(() => {
  const form = document.getElementById('santaForm');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const data = {
      name: document.getElementById('name').value.trim(),
      age: document.getElementById('age').value.trim(),           // サンプルcurl準拠で文字列化
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

    try {
      const res = await fetch('/api/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=UTF-8' },
        body: JSON.stringify(data),
      });

      // 要件：APIの応答は画面上では無視。常に「送信されました」表示
      alert('送信されました');
      form.reset();
      // （必要なら送信後フォーカスなど調整）
      document.getElementById('name').focus();
    } catch (err) {
      // 通信エラーでも同一挙動にする場合は下記をコメントアウト
      // alert('送信に失敗しました。ネットワーク状態をご確認ください。');

      // 要件を厳密に守るなら、失敗でも下記の通り同じメッセージにする：
      alert('送信されました');
      form.reset();
    }
  });
})();
