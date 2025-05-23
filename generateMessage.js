import fs from 'fs';
import path from 'path';

const summaryPath = 'build/allure-report/widgets/summary.json';
const testCasesDir = 'build/allure-report/data/test-cases/';

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf-8'));
const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

const branch = process.env.GITHUB_REF_NAME || 'unknown';
const time = process.env.TIME || 'неизвестно';
const repo = process.env.REPO || 'unknown/repo';
const runId = process.env.RUN_ID || '0';

const allureLink = `https://github.com/${repo}/actions/runs/${runId}`;
const logsLink = `https://github.com/${repo}/actions/runs/${runId}`;
const runResult = failed > 0 ? 'completed with errors' : 'passed';
const statusText = failed > 0 ? '🔴 Тесты с ошибками — требуется анализ.' : '🟢 Все тесты прошли успешно';

// Рекурсивный рендер шагов
function renderSteps(steps, indent = 0) {
  if (!steps) return '';
  const pad = '  '.repeat(indent);
  return steps.map(step => {
    const icon =
      step.status === 'passed' ? '✅' :
      step.status === 'failed' ? '❌' :
      step.status === 'skipped' ? '⏭' : '🔹';
    let line = `${pad}- ${icon} ${step.name}`;
    if (step.steps && step.steps.length > 0) {
      line += '\n' + renderSteps(step.steps, indent + 1);
    }
    return line;
  }).join('\n');
}

// Сборка отчёта по фейлам
let failedTests = '';

if (fs.existsSync(testCasesDir)) {
  fs.readdirSync(testCasesDir).forEach(file => {
    const test = JSON.parse(fs.readFileSync(path.join(testCasesDir, file), 'utf-8'));
    if (test.status === 'failed') {
      failedTests += `\n*${test.name}*\n📝 Steps:\n`;
      failedTests += test.steps?.length ? renderSteps(test.steps) : '- ⚠️ Шаги не найдены\n';
    }
  });
} else {
  failedTests += '\n⚠️ Шаги не найдены. Папка `data/test-cases` отсутствует.\n';
}

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

