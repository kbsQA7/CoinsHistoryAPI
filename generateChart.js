import puppeteer from 'puppeteer';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Путь к summary.json
const summaryPath = path.join(__dirname, 'build', 'reports', 'allure-report', 'allureReport', 'widgets', 'summary.json');

if (!fs.existsSync(summaryPath)) {
  console.error('❌ summary.json не найден. Убедись, что Allure отчёт был сгенерирован.');
  process.exit(1);
}

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf-8'));
const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;


const html = `
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <style>
      body {
        margin: 0;
        padding: 0;
        background: white;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    </style>
  </head>
  <body>
    <canvas id="chart" width="800" height="800"></canvas>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
      const ctx = document.getElementById('chart').getContext('2d');
      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['✅ Passed', '❌ Failed', '⚠️ Skipped'],
          datasets: [{
            label: 'Test Results',
            data: [${passed}, ${failed}, ${skipped}],
            backgroundColor: ['#4caf50', '#f44336', '#ff9800'],
            borderColor: ['#388e3c', '#d32f2f', '#f57c00'],
            borderWidth: 2,
          }]
        },
        options: {
          plugins: {
            legend: { position: 'bottom' },
            title: {
              display: true,
              text: 'Allure Test Results Summary',
              font: {
                size: 22
              }
            }
          }
        }
      });
    </script>
  </body>
</html>
`;

const htmlPath = path.join(__dirname, 'temp_chart.html');
fs.writeFileSync(htmlPath, html);


const browser = await puppeteer.launch({ headless: 'new', args: ['--no-sandbox'] });
const page = await browser.newPage();

await page.goto(`file://${htmlPath}`, { waitUntil: 'networkidle0' });
await page.screenshot({ path: 'allure-summary-chart.png' });
await browser.close();

console.log('✅ Диаграмма успешно сгенерирована: allure-summary-chart.png');
