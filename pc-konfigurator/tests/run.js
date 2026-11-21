/* PC-Werkbank – Oberflächentests (Playwright)
   Aufruf:  node pc-konfigurator/tests/run.js            (prüft pc-konfigurator/index.html)
            node pc-konfigurator/tests/run.js datei.html  (andere Datei)
   Voraussetzung: Node.js und das Paket „playwright“ mit Chromium. */
const {chromium}=require('playwright');
const path=require('path');
const FILE=path.resolve(process.argv[2]||path.join(__dirname,'..','index.html'));
const URL='file://'+FILE;
let pass=0,fail=0;const fails=[];
const ok=(name,cond,info)=>{if(cond){pass++;console.log('  ✓',name);}else{fail++;fails.push(name);console.log('  ✗',name,info!==undefined?'→ '+JSON.stringify(info).slice(0,300):'');}};

(async()=>{
  const b=await chromium.launch();
  const ctx=await b.newContext({viewport:{width:1300,height:1000}});
  const p=await ctx.newPage();
  const errs=[];p.on('pageerror',e=>errs.push(e.message));
  const set=(id,v)=>p.$eval('#sl-'+id,(el,v)=>{el.value=v;el.dispatchEvent(new Event('input',{bubbles:true}));},String(v));
  const picks=()=>p.evaluate(()=>{const st=JSON.parse(localStorage.getItem('pcwerkbank.v1'));return Object.fromEntries(Object.entries(st.picks).map(([k,v])=>[k,v.id]));});
  const problems=()=>p.evaluate(()=>[...document.querySelectorAll('#summary .issues li')].map(l=>l.textContent.replace(/\s+/g,' ')).filter(t=>/^(Konflikt|Achtung)/.test(t)));

  console.log('Start');
  await p.goto(URL);await p.waitForTimeout(400);
  ok('lädt ohne JavaScript-Fehler',errs.length===0,errs);
  ok('Startkonfiguration hat 8 Teile',Object.keys(await picks()).length>=8,await picks());
  ok('Hero zeigt einen Preis',/€/.test(await p.textContent('#heroPrice')));
  ok('Kopfleiste zeigt FPS',/FPS/.test(await p.textContent('#hud')));

  console.log('Ansichten');
  for(const v of ['dash','ov','cmp','konfig']){await p.click('#tab-'+v);await p.waitForTimeout(250);}
  ok('alle vier Ansichten ohne Fehler',errs.length===0,errs);

  console.log('Regler-Kombinationen (Budget, Auflösung, Gaming/Kreativ, Größe)');
  await p.click('#tab-dash');
  let combos=0;const bad=[];
  for(const mix of [0,50,100])for(const res of [0,1,3])for(const bud of [8050,1500,2500])for(const size of [0,2]){
    await set('mix',mix);await set('res',res);await set('budget',bud);await set('size',size);await set('quiet',50);await set('ram',1);await set('storage',1);
    combos++;const pr=await problems();if(pr.length)bad.push({mix,res,bud,size,pr});
  }
  ok(`${combos} Kombinationen ohne Konflikt oder Warnung`,bad.length===0,bad.slice(0,3));
  ok('keine JavaScript-Fehler bei den Reglern',errs.length===0,errs);

  console.log('Vergleiche');
  await p.click('#tab-dash');await p.waitForTimeout(600);
  ok('Preis-FPS-Diagramm aller Systeme',await p.$$eval('#dSysPlot circle',x=>x.length)>10);
  ok('Ranking zeigt den eigenen PC',(await p.textContent('#dRank')).includes('Dein Selbstbau'));
  const first=await p.$('#dPrebuilt tr[data-presel]');
  if(first){await first.click();await p.waitForTimeout(200);}
  ok('Detailvergleich Fertig-PC',(await p.textContent('#dPreDetail')).includes('Detailvergleich'));
  ok('Spieletabelle mit Upscaling-Schaltern',await p.$$eval('#dApps [data-gups]',x=>x.length)===3);
  await p.click('#tab-cmp');await p.waitForTimeout(250);
  for(const i of [1,2]){await p.locator('[data-cmppick]').nth(i).check();await p.waitForTimeout(150);}
  ok('zwei Teile Seite an Seite',(await p.textContent('#cSide')).includes('2 im Vergleich'));

  console.log('Aufrüst-Berater, Bauanleitung, Einkauf');
  await p.click('#tab-up');await p.waitForTimeout(300);
  await p.selectOption('#upCpu','o-r5-5600');await p.selectOption('#upGpu','o-rtx3060');await p.waitForTimeout(150);
  ok('Aufrüst-Berater empfiehlt Upgrades',await p.$$eval('#upOut tbody tr',x=>x.length)>=3);
  await p.click('#tab-ov');await p.waitForTimeout(300);
  const s1=await p.textContent('#bgStep');await p.click('[data-act=bgNext]');await p.waitForTimeout(100);
  ok('Bauanleitung blättert weiter',s1!==await p.textContent('#bgStep'));
  ok('Bauanleitung hebt Bauteil hervor',await p.$$eval('#bgPc .has-focus',x=>x.length)===1);
  ok('Einkaufsliste mit Geizhals-Links',await p.$$eval('#shopOut a[href*="geizhals"]',x=>x.length)>=8);
  console.log('Zweite SSD');
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  await p.click('#slots [data-act=pick][data-cat=ssd2]');await p.click('#plist [data-id=crucial-p310-2]');await p.waitForTimeout(250);
  ok('zweite SSD gewählt',(await picks()).ssd2==='crucial-p310-2');
  await p.click('#slots [data-act=remove][data-cat=ssd2]');await p.waitForTimeout(150);

  console.log('Speichern und Code');
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  await p.click('#hero [data-act="openSave"]');await p.waitForTimeout(200);
  ok('Speichern-Dialog öffnet',await p.isVisible('#saveDlg'));
  await p.keyboard.press('Escape');

  console.log('Handy (390 px)');
  const m=await b.newPage({viewport:{width:390,height:844}});m.on('pageerror',e=>errs.push(e.message));
  await m.goto(URL);await m.waitForTimeout(400);
  const widths=[];for(const v of ['konfig','dash','ov','cmp','up']){await m.click('#tab-'+v);await m.waitForTimeout(300);widths.push(await m.evaluate(()=>document.documentElement.scrollWidth));}
  ok('kein horizontales Scrollen in allen Ansichten',widths.every(w=>w<=390),widths);

  ok('insgesamt keine JavaScript-Fehler',errs.length===0,errs);
  await b.close();
  console.log(`\n${pass} bestanden, ${fail} fehlgeschlagen`);
  if(fail){console.log('Fehlgeschlagen:',fails.join(' | '));process.exit(1);}
})().catch(e=>{console.error(e);process.exit(1);});
