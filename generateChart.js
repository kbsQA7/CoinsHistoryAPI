import puppeteer from 'puppeteer';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const summaryPath = path.join(__dirname, 'build', 'reports', 'allure-report', 'allureReport', 'widgets', 'summary.json');

if (!fs.existsSync(summaryPath)) {
  console.error('❌ summary.json не найден.');
  process.exit(1);
}

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf-8'));
const { passed = 0, failed = 0, skipped = 0 } = summary.statistic;


const labels = [];
const values = [];
const colors = [];
const borders = [];

if (passed > 0) {
  labels.push('✅ Passed: ' + passed);
  values.push(passed);
  colors.push('#4caf50');
  borders.push('#388e3c');
}
if (failed > 0) {
  labels.push('❌ Failed: ' + failed);
  values.push(failed);
  colors.push('#f44336');
  borders.push('#d32f2f');
}
if (skipped > 0) {
  labels.push('⚠️ Skipped: ' + skipped);
  values.push(skipped);
  colors.push('#ff9800');
  borders.push('#f57c00');
}

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
        height: 100vh;
      }
    </style>
  </head>
  <body>
    <canvas id="chart" width="600" height="600"></canvas>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels"></script>
    <script>
      const labels = ${JSON.stringify(labels)};
      const values = ${JSON.stringify(values)};
      const colors = ${JSON.stringify(colors)};
      const borders = ${JSON.stringify(borders)};

      const ctx = document.getElementById('chart').getContext('2d');
      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [{
            data: values,
            backgroundColor: colors,
            borderColor: borders,
            borderWidth: 2
          }]
        },
        options: {
          layout: {
            padding: {
              top: 30,
              bottom: 30
            }
          },
          plugins: {
            datalabels: {
              color: '#fff',
              font: {
                weight: 'bold',
                size: 14
              },
              formatter: (value, ctx) => {
                return ctx.chart.data.labels[ctx.dataIndex];
              },
              anchor: 'center',
              align: 'center'
            },
            legend: {
              display: true,
              position: 'bottom'
            },
            title: {
              display: true,
              text: 'CoinsHistoryAPI Tests Results Chart',
              font: {
                size: 20
              }
            },
            tooltip: {
              callbacks: {
                label: (context) => context.label
              }
            }
          }
        },
        plugins: [ChartDataLabels]
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




