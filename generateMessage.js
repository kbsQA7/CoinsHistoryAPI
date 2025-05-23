import fs from 'fs';
const summary = JSON.parse(fs.readFileSync('build/reports/allure-report/allureReport/widgets/summary.json', 'utf-8'));
const results = JSON.parse(fs.readFileSync('build/reports/allure-report/allureReport/widgets/result.json', 'utf-8'));

const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

let failedTests = '';
if (failed > 0) {
  results.forEach(test => {
    if (test.status === 'failed') {
      failedTests += `\n*${test.name}*\n📝 Steps:\n`;
      test.steps?.forEach(step => {
        const icon = step.status === 'passed' ? '✅' : (step.status === 'failed' ? '❌' : '⏭');
        failedTests += `- ${icon} ${step.name}\n`;
      });
    }
  });
}

const message = `
✅ Scheduled run tests ${failed > 0 ? 'completed with errors' : 'passed'}
🧪 *Проект:* CoinsHistoryAPI
🔗 [Репозиторий](https://github.com/kbsQA7/CoinsHistoryAPI)
🕒 *Время запуска:* ${{ steps.get-time.outputs.time }}
🔁 *Бранч:* ${{ github.ref_name }}
⚙️ *CI:* GitHub Actions

📊 *Результаты:*
✅ Пройдено: ${passed}
❌ Провалено: ${failed}
⏭ Пропущено: ${skipped}

${failedTests ? `🧨 *Упавшие тесты:*${failedTests}` : ''}

📎 [Allure-отчёт](https://your-allure-url.com)
📁 [Логи CI](https://your-ci-logs-url.com)

🛠️ *Ответственный:* @kbsQA7
📢 *Статус:* ${failed > 0 ? '🔴 Тесты с ошибками — требуется анализ.' : '🟢 Все тесты прошли успешно'}
`;

console.log(message.trim());
