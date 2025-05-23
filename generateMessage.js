import fs from 'fs';
import path from 'path';

const summaryPath = 'build/reports/allure-report/allureReport/widgets/summary.json';
const suitesPath = 'build/reports/allure-report/allureReport/widgets/suites.json';

if (!fs.existsSync(summaryPath)) {
  console.error('❌ summary.json не найден.');
  process.exit(1);
}

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf-8'));
const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;

let failedTests = '';

if (fs.existsSync(suitesPath)) {
  const suites = JSON.parse(fs.readFileSync(suitesPath, 'utf-8'));

  // Рекурсивно найти все failed тесты
  function findFailedTests(items) {
    for (const item of items) {
      if (item.children && item.children.length > 0) {
        findFailedTests(item.children);
      } else if (item.status === 'failed') {
        failedTests += `\n❌ ${item.name}`;
      }
    }
  }

  findFailedTests(suites.children || suites);
} else {
  failedTests = '\n⚠️ Упавшие тесты не найдены (нет suites.json)';
}

const branch = process.env.GITHUB_REF_NAME || 'unknown';
const time = process.env.TIME || 'неизвестно';
const repo = process.env.REPO || 'unknown/repo';
const runId = process.env.RUN_ID || '0';

const allureLink = `https://github.com/${repo}/actions/runs/${runId}`;
const logsLink = `https://github.com/${repo}/actions/runs/${runId}`;
const runResult = failed > 0 ? 'completed with errors' : 'passed';
const statusText = failed > 0 ? '🔴 Тесты с ошибками — требуется анализ.'



