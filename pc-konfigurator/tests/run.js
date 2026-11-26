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
  const ctx=await b.newContext({viewport:{width:1300,height:1000},acceptDownloads:true});
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
  for(const v of ['dash','ov','cmp','up','konfig']){await p.click('#tab-'+v);await p.waitForTimeout(250);}
  ok('alle fünf Ansichten ohne Fehler',errs.length===0,errs);

  console.log('Regler-Kombinationen (Budget, Auflösung, Gaming/Kreativ, Größe)');
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  const kept0=await p.evaluate(()=>JSON.parse(localStorage.getItem('pcwerkbank.v1')).picks);
  await p.click('#tab-dash');
  await set('quiet',70);await p.waitForTimeout(200);
  const kept1=await p.evaluate(()=>JSON.parse(localStorage.getItem('pcwerkbank.v1')).picks);
  ok('Regler ändern behaltene Teile nicht',Object.keys(kept0).every(c=>kept0[c].src!=='user'||(kept1[c]&&kept1[c].id===kept0[c].id&&kept1[c].src==='user')));
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  if(await p.isEnabled('#slots [data-act=releaseAll]')){await p.click('#slots [data-act=releaseAll]');await p.waitForTimeout(300);}
  await p.click('#tab-dash');
  let combos=0;const bad=[];
  for(const mix of [0,50,100])for(const res of [0,1,3])for(const bud of [8050,1500,2500])for(const size of [0,2]){
    await set('mix',mix);await set('res',res);await set('budget',bud);await set('size',size);await set('quiet',50);await set('ram',1);await set('storage',1);
    combos++;const pr=await problems();if(pr.length)bad.push({mix,res,bud,size,pr});
  }
  ok(`${combos} Kombinationen ohne Konflikt oder Warnung`,bad.length===0,bad.slice(0,3));
  ok('keine JavaScript-Fehler bei den Reglern',errs.length===0,errs);

  console.log('Budget bindend und Behalten');
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  if(await p.isEnabled('#slots [data-act=releaseAll]')){await p.click('#slots [data-act=releaseAll]');await p.waitForTimeout(300);}
  const overB=[];
  for(const bud of [900,1300,1800,2600,4000]){await p.fill('#budget',String(bud));await p.waitForTimeout(800);
    const sm=await p.textContent('#summary');if(/drüber/.test(sm))overB.push(bud);}
  ok('Budget wird nie überschritten (900–4000 €)',overB.length===0,overB);
  await p.click('#slots [data-act=keep][data-cat=gpu]');await p.waitForTimeout(200);
  const g1=(await picks()).gpu;
  await p.fill('#budget','2200');await p.waitForTimeout(800);
  const pk=await p.evaluate(()=>JSON.parse(localStorage.getItem('pcwerkbank.v1')).picks);
  ok('behaltenes Teil bleibt bei Budgetänderung',pk.gpu.id===g1&&pk.gpu.src==='user',pk.gpu);
  ok('Leiste zeigt behaltene Teile',/1 von 8/.test(await p.textContent('.slot-bar')));
  await p.click('#slots [data-act=release][data-cat=gpu]');await p.waitForTimeout(300);
  ok('Freigeben macht das Teil wieder automatisch',(await p.evaluate(()=>JSON.parse(localStorage.getItem('pcwerkbank.v1')).picks.gpu.src))==='auto');
  await p.selectOption('#bBasis','alt');await p.selectOption('#bReserve','5');await p.fill('#budget','2500');await p.waitForTimeout(900);
  const hs=await p.textContent('#heroPriceSub');
  ok('Budget für Alternate-Einkauf mit Reserve eingehalten',/bei Alternate/.test(hs)&&/unter Budget 2\.375/.test(hs),hs);
  await p.selectOption('#bBasis','best');await p.selectOption('#bReserve','0');
  await p.fill('#budget','');await p.waitForTimeout(800);

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

  console.log('Vergleich: Score, Unterschied, Filter');
  await p.click('#tab-cmp');await p.waitForTimeout(250);
  ok('„Für dich“-Spalte vorhanden',await p.$$eval('th[data-sort="fit"]',x=>x.length)===1);
  ok('Unterschied zu deinem Teil',await p.$$eval('th[data-sort="dprice"]',x=>x.length)===1);
  await p.check('#cQuiet');await p.waitForTimeout(150);const nq=await p.$$eval('#cTable tbody tr',x=>x.length);await p.uncheck('#cQuiet');await p.waitForTimeout(150);
  ok('Filter „nur leise“ wirkt',nq>0&&nq<await p.$$eval('#cTable tbody tr',x=>x.length));
  console.log('PDF');
  await p.click('#tab-konfig');await p.waitForTimeout(200);
  await p.click('#hero [data-act="openPdf"]');await p.waitForTimeout(150);
  ok('PDF-Dialog mit 15 Abschnitten',await p.$$eval('[data-pdfsec]',x=>x.length)===15);
  await p.click('[data-act="pdfAll"]');
  try{const [d]=await Promise.all([p.waitForEvent('download',{timeout:60000}),p.click('#pdfGo')]);const fp=await d.path();const sz=require('fs').statSync(fp).size;ok('PDF wird erzeugt ('+Math.round(sz/1024)+' KB)',sz>50000&&sz<3e6,sz);}
  catch(e){ok('PDF wird erzeugt',false,String(e).slice(0,120));}
  await p.click('[data-act="closePdf"]');

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
