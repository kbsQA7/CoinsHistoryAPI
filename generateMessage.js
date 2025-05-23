import fs from 'fs';
import path from 'path';

const summaryPath = 'build/reports/allure-report/allureReport/widgets/summary.json';
const suitesPath = 'build/reports/allure-report/allureReport/widgets/suites.json';

// Проверка, что summary вообще есть
if (!fs.existsSync(summaryPath)) {
  console.error('❌ summary.json не найден.');
  process.exit(1);
}

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf-8'));
const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

let failedTests = '';

// Проверяем suites.json и ищем упавшие тесты
if (fs.existsSync(suitesPath)) {
  const suites = JSON.parse(fs.readFileSync(suitesPath, 'utf-8'));

  function collectFailedTests(items) {
    for (const item of items) {
      if (item.children && item.children.length > 0) {
        collectFailedTests(item.children);
      } else if (item.status === 'failed') {
        failedTests += `\n❌ ${item.name}`;
      }
    }
  }

  collectFailedTests(suites.children || suites);
}

const branch = process.env.GITHUB_REF_NAME || 'unknown';
const time = process.env.TIME || 'неизвестно';
const repo = process.env.REPO || 'unknown/repo';
const runId = process.env.RUN_ID || '0';

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
${failed > 0 ? `\n🧨 *Упавшие тесты:*${failedTests}` : ''}

📎 [Allure-отчёт](${allureLink})
📁 [Логи CI](${logsLink})

🛠️ *Ответственный:* @kbsQA7
📢 *Статус:* ${statusText}
`;

console.log(message.trim());




