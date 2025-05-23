import fs from 'fs';
import path from 'path';

// Пути к Allure JSON
const basePath = 'build/reports/allure-report/allureReport/widgets/';
const summary = JSON.parse(fs.readFileSync(path.join(basePath, 'summary.json'), 'utf-8'));
const results = JSON.parse(fs.readFileSync(path.join(basePath, 'result.json'), 'utf-8'));

const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

// Получаем GitHub context из ENV
const branch = process.env.GITHUB_REF_NAME || 'unknown';
const time = process.env.TIME || 'неизвестно';

// Обрабатываем упавшие тесты
let failedTests = '';
if (failed > 0) {
  results.forEach(test => {
    if (test.status === 'failed') {
      failedTests += `\n*${test.name}*\n📝 Steps:\n`;
      if (test.steps?.length) {
        test.steps.forEach(step => {
          const icon = step.status === 'passed' ? '✅' :
                       step.status === 'failed' ? '❌' :
                       step.status === 'skipped' ? '⏭' : '🔹';
          failedTests += `- ${icon} ${step.name}\n`;
        });
      } else {
        failedTests += `- ⚠️ Шаги не найдены\n`;
      }
    }
  });
}

// Статус
const statusText = failed > 0 ? '🔴 Тесты с ошибками — требуется анализ.' : '🟢 Все тесты прошли успешно';
const runResult = failed > 0 ? 'completed with errors' : 'passed';

// Финальное сообщение
const message = `
✅ Scheduled run tests ${runResult}
🧪 *Проект:* CoinsHistoryAPI
🔗 [Репозиторий](https://github.com/kbsQA7/CoinsHistoryAPI)
🕒 *Время запуска:* ${time}
🔁 *Бранч:* ${branch}
⚙️ *CI:* GitHub Actions

📊 *Результаты:*
✅ Пройдено: ${passed}
❌ Провалено: ${failed}
⏭ Пропущено: ${skipped}

${failedTests ? `🧨 *Упавшие тесты:*${failedTests}` : ''}

🛠️ *Ответственный:* @kbsQA7
📢 *Статус:* ${statusText}
`;

console.log(message.trim());
