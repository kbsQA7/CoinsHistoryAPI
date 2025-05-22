

import puppeteer from 'puppeteer';
import path from 'path';
import { fileURLToPath } from 'url';
import { existsSync } from 'fs';


const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);


const reportPath = path.resolve(__dirname, 'build', 'reports', 'allure-report', 'allureReport', 'index.html');



if (!existsSync(reportPath)) {
  console.error('❌ Allure report index.html not found. Make sure it is generated before running this script.');
  process.exit(1);
}


const htmlFileUrl = `file://${reportPath}`;


const screenshotPath = path.resolve(__dirname, 'allure-summary.png');


const browser = await puppeteer.launch({
  headless: 'new',
  defaultViewport: { width: 1400, height: 1000 },
  args: ['--no-sandbox', '--disable-setuid-sandbox'] 
});

const page = await browser.newPage();

await page.goto(htmlFileUrl, { waitUntil: 'networkidle0' });
await page.screenshot({ path: screenshotPath });

await browser.close();

console.log(`✅ Screenshot saved to ${screenshotPath}`);
