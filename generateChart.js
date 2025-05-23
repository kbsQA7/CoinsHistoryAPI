import puppeteer from 'puppeteer';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const summaryPath = path.join(__dirname, 'build', 'allure-report', 'widgets', 'summary.json');
if (!fs.existsSync(summaryPath)) {
  console.error('❌ summary.json не найден по пути:', summaryPath);
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
      body { margin: 0; padding: 0; background: white; display: flex; align-items: center; justify-content: center; height: 100vh; }
    </style>
  </head>
  <body>
    <canvas id="chart" width="600" height="600"></canvas>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
      new Chart(document.getElementById('chart'), {
        type: 'doughnut',
        data: {
          labels: ['✅ Passed: ${passed}', '❌ Failed: ${failed}', '⚠️ Skipped: ${skipped}'],
          datasets: [{
            data: [${passed}, ${failed}, ${skipped}],
            backgroundColor: ['#4caf50', '#f44336', '#ff9800'],
            borderColor: ['#388e3c', '#d32f2f', '#f57c00'],
            borderWidth: 2
          }]
        },
        options: {
          plugins: {
            legend: { position: 'bottom' },
            title: {
              display: true,
              text: 'CoinsHistoryAPI Test Results',
              font: { size: 20 }
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





