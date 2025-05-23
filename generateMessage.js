import fs from 'fs';
import path from 'path';

const summary = JSON.parse(fs.readFileSync('build/reports/allure-report/allureReport/widgets/summary.json', 'utf-8'));
const testCasesDir = 'build/reports/allure-report/allureReport/data/test-cases/';

const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

const branch = process.env.GITHUB_REF_NAME || 'unknown';
const time = process.env.TIME || 'неизвестно';
const repo = process.env.REPO || 'unknown/repo';
const runId = process.env.RUN_ID || '0';

let failedTests = '';

fs.readdirSync(testCasesDir).forEach(file => {
  const test = JSON.parse(fs.readFileSync(path.join(testCasesDir, file), 'utf-8'));
  if (test.status === 'failed') {
    failedTests += `\n*${test.name}*\n📝 Steps:\n`;
    if (test.steps?.length) {
      test.steps.forEach(step => {
        const icon =
          step.status === 'passed' ? '✅' :
          step.status === 'failed' ? '❌' :
          step.status === 'skipped' ? '⏭' : '🔹';
        failedTests += `- ${icon} ${step.name}\n`;
      });
    } else {
      failedTests += `- ⚠️ Шаги не найдены\n`;
    }
  }
});

const allureLink = `https://github.com/${repo}/actions/runs/${runId}`;
const logsLink = `https://github.com/${repo}/actions/runs/${runId}`;
const runResult = failed > 0 ? 'completed with errors' : 'passed';
const statusText = failed > 0 ? '🔴 Тесты с ошибками — требуется анализ.' : '🟢 Все тесты прошли успешно';

const message = `
✅ Scheduled run tests ${runResult}
🧪 *Проект:* CoinsHistoryAPI
🔗 [Репозиторий](https://github.com/${repo})
🕒 *Время запуска:* ${time}
🔁 *Бранч:* ${branch}
⚙️ *CI:* GitHub Actions

📊 *Результаты:*
✅ Пройдено: ${passed}
❌ Провалено: ${failed}
⏭ Пропущено: ${skipped}

${failedTests ? `🧨 *Упавшие тесты:*${failedTests}` : ''}

📎 [Allure-отчёт](${allureLink})
📁 [Логи CI](${logsLink})

🛠️ *Ответственный:* @kbsQA7
📢 *Статус:* ${statusText}
`;

console.log(message.trim());
