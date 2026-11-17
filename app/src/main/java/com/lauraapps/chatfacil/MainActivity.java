package com.lauraapps.chatfacil;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.animation.AlphaAnimation;
import android.net.Uri;
import android.os.Bundle;
import android.content.res.Configuration;
import android.text.InputType;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.google.android.gms.ads.*;
import com.google.android.ump.*;
import org.json.*;
import java.util.regex.*;

public class MainActivity extends Activity {
  private static final int INK=0xff152b40, MUTE=0xff63788a, GREEN=0xff087f76, BLUE=0xff337bea, BG=0xfff4f8fb, SOFT=0xffe4f5f1, GOLD=0xffffc775;
  private final String[] codes={"1","52","57","34","1","54","51","56","55","58"};
  private final int[] countries={R.string.country_dominican,R.string.country_mexico,R.string.country_colombia,R.string.country_spain,R.string.country_us,R.string.country_argentina,R.string.country_peru,R.string.country_chile,R.string.country_brazil,R.string.country_venezuela};
  private LinearLayout base, area, adsArea;
  private EditText phoneField, msgField;
  private Spinner countryField, appField;
  private String phone="", msg="";
  private int tab=0, selectedCountry=0, selectedApp=0;
  private JSONArray history=new JSONArray(), favorites=new JSONArray(), templates=new JSONArray();
  private android.content.SharedPreferences prefs;
  private ConsentInformation consent;
  private boolean adsReady=false;
  private AdView banner;
  private TextView liveStatus;

  @Override protected void attachBaseContext(Context base){super.attachBaseContext(Language.wrap(base));}
  @Override public void onCreate(Bundle state){
    super.onCreate(state);
    getWindow().setStatusBarColor(INK);
    prefs=getSharedPreferences("chat_facil_private",MODE_PRIVATE);
    history=load("history"); favorites=load("favorites"); templates=load("templates");
    selectedCountry=prefs.getInt("selected_country",defaultCountry());
    selectedApp=prefs.getInt("selected_app",0);
    readShare(getIntent());draw();requestConsent();
  }
  @Override protected void onNewIntent(Intent intent){
    super.onNewIntent(intent);setIntent(intent);readShare(intent);tab=0;draw();
  }
  private int defaultCountry(){
    String country=java.util.Locale.getDefault().getCountry();
    switch(country){case "MX":return 1;case "CO":return 2;case "ES":return 3;case "US":return 4;case "AR":return 5;case "PE":return 6;case "CL":return 7;case "BR":return 8;case "VE":return 9;default:return 0;}
  }
  private JSONArray load(String key){try{return new JSONArray(prefs.getString(key,"[]"));}catch(Exception e){return new JSONArray();}}
  private void save(String key,JSONArray arr){prefs.edit().putString(key,arr.toString()).apply();}
  private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  private LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
  private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
  private GradientDrawable shape(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
  private GradientDrawable outline(int color,int stroke,int radius){GradientDrawable g=shape(color,radius);g.setStroke(dp(1),stroke);return g;}
  private GradientDrawable gradient(int first,int second,int radius){GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{first,second});g.setCornerRadius(dp(radius));return g;}
  private void addCard(LinearLayout parent,LinearLayout content,int margin){content.setPadding(dp(17),dp(15),dp(17),dp(17));content.setBackground(outline(Color.WHITE,0xffe3ecf2,20));content.setElevation(dp(2));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(margin);p.bottomMargin=dp(8);parent.addView(content,p);}
  private TextView caption(String text){TextView t=text(text,13,false);t.setTextColor(MUTE);return t;}
  private void showFeedback(View view){AlphaAnimation a=new AlphaAnimation(.55f,1f);a.setDuration(260);view.startAnimation(a);}
  private Button accentButton(int id,Runnable action){Button b=button(id,action);b.setTextColor(Color.WHITE);b.setTextSize(15);b.setTypeface(null,Typeface.BOLD);b.setBackgroundTintList(ColorStateList.valueOf(GREEN));b.setMinHeight(dp(54));return b;}
  private TextView chip(String title,Runnable action){TextView t=text(title,13,true);t.setTextColor(GREEN);t.setPadding(dp(12),dp(10),dp(12),dp(10));t.setBackground(outline(SOFT,0xffbfe6dd,30));t.setOnClickListener(v->{showFeedback(v);action.run();});return t;}
  private void space(LinearLayout parent,int h){View v=new View(this);parent.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
  private TextView text(String value,int size,boolean bold){
    TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(INK);
    t.setPadding(0,dp(5),0,dp(5));if(bold)t.setTypeface(null,Typeface.BOLD);return t;
  }
  private Button button(int id,Runnable click){
    Button b=new Button(this);b.setAllCaps(false);b.setText(id);b.setTextColor(INK);b.setTextSize(14);b.setMinHeight(dp(46));b.setOnClickListener(v->{showFeedback(v);click.run();});return b;
  }
  private EditText edit(String initial,int hint,int flags){
    EditText e=new EditText(this);e.setText(initial);e.setHint(hint);e.setTextSize(16);e.setInputType(flags);e.setSingleLine((flags & InputType.TYPE_TEXT_FLAG_MULTI_LINE)==0);e.setPadding(dp(14),dp(10),dp(14),dp(10));e.setBackground(outline(Color.WHITE,0xffcbdde7,13));return e;
  }
  private void label(int id){TextView t=text(getString(id),14,true);t.setPadding(0,dp(12),0,dp(7));area.addView(t);}
  private void hint(int id){TextView t=text(getString(id),12,false);t.setTextColor(MUTE);area.addView(t);}
  private Spinner spinner(String[] labels,int choice){
    Spinner s=new Spinner(this);s.setPadding(dp(10),dp(8),dp(8),dp(8));s.setBackground(outline(Color.WHITE,0xffcbdde7,13));ArrayAdapter<String> a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,labels);s.setAdapter(a);
    s.setSelection(Math.max(0,Math.min(choice,labels.length-1)));return s;
  }
  private String[] countryLabels(){
    String[] labels=new String[countries.length];
    for(int i=0;i<labels.length;i++)labels[i]=getString(countries[i])+" (+"+codes[i]+")";
    return labels;
  }
  private void rememberFields(){
    if(tab==0 && phoneField!=null && msgField!=null){
      phone=phoneField.getText().toString();msg=msgField.getText().toString();
      selectedCountry=countryField.getSelectedItemPosition();selectedApp=appField.getSelectedItemPosition();
      prefs.edit().putInt("selected_country",selectedCountry).putInt("selected_app",selectedApp).apply();
    }
  }
  private void draw(){
    phoneField=null;msgField=null;
    base=col();base.setBackgroundColor(BG);setContentView(base);
    LinearLayout head=col();head.setPadding(dp(21),dp(21),dp(21),dp(17));head.setBackground(gradient(0xff123b56,0xff087f76,0));
    TextView eyebrow=text("LAURAAPPS  •  CHAT",11,true);eyebrow.setTextColor(GOLD);head.addView(eyebrow);
    TextView title=text(getString(R.string.app_name),21,true);title.setTextColor(-1);head.addView(title);
    TextView sub=text(getString(R.string.subtitle),12,false);sub.setTextColor(0xffcad9df);head.addView(sub);base.addView(head);
    LinearLayout tabs=row();tabs.setBackgroundColor(-1);int[] names={R.string.chat,R.string.recent,R.string.templates,R.string.settings};
    String[] symbols={"✉  ","◷  ","▤  ","⚙  "};
    for(int i=0;i<4;i++){final int j=i;TextView t=text(symbols[i]+getString(names[i]),12,i==tab);t.setGravity(Gravity.CENTER);t.setTextColor(i==tab?GREEN:MUTE);if(i==tab)t.setBackgroundColor(0xffe7f7f3);
      t.setPadding(dp(1),dp(12),dp(1),dp(12));tabs.addView(t,new LinearLayout.LayoutParams(0,dp(51),1));
      t.setOnClickListener(v->{showFeedback(v);rememberFields();tab=j;draw();});
    }base.addView(tabs);
    ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);area=col();area.setPadding(dp(16),dp(15),dp(16),dp(22));scroll.addView(area);base.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    if(tab==0)drawChat();else if(tab==1)drawRecent();else if(tab==2)drawTemplates();else drawSettings();
    adsArea=col();base.addView(adsArea);showBanner();
  }
  private void readShare(Intent intent){
    if(intent==null || !Intent.ACTION_SEND.equals(intent.getAction()) || !"text/plain".equals(intent.getType()))return;
    String incoming=intent.getStringExtra(Intent.EXTRA_TEXT);if(incoming==null)return;
    Matcher matcher=Pattern.compile("(?<!\\d)(?:\\+|00)?[0-9][0-9 ()\\-.]{6,24}[0-9](?!\\d)").matcher(incoming);
    if(matcher.find()){
      phone=matcher.group().trim();
      String remaining=(incoming.substring(0,matcher.start())+incoming.substring(matcher.end())).trim();
      if(!remaining.isEmpty() && remaining.length()<2000)msg=remaining;
    }else if(incoming.length()<2000)msg=incoming;
    Toast.makeText(this,R.string.received,Toast.LENGTH_SHORT).show();
  }
  private void drawChat(){
    LinearLayout hero=col();hero.setPadding(dp(18),dp(20),dp(18),dp(20));hero.setBackground(gradient(0xffe1f8f1,0xffe7f0ff,20));
    TextView dot=text("●  "+getString(R.string.quick_ready),12,true);dot.setTextColor(GREEN);hero.addView(dot);
    TextView heroTitle=text(getString(R.string.hero_title),23,true);hero.addView(heroTitle);
    hero.addView(caption(getString(R.string.hero_subtitle)));area.addView(hero);showFeedback(hero);
    LinearLayout form=col();addCard(area,form,14);
    TextView formTitle=text("✆  "+getString(R.string.number),17,true);form.addView(formTitle);
    TextView countryLabel=text(getString(R.string.country),13,true);form.addView(countryLabel);
    countryField=spinner(countryLabels(),selectedCountry);form.addView(countryField);
    TextView numberLabel=text(getString(R.string.number),13,true);form.addView(numberLabel);
    phoneField=edit(phone,R.string.number_hint,InputType.TYPE_CLASS_PHONE);form.addView(phoneField);
    liveStatus=caption(getString(R.string.number_empty));form.addView(liveStatus);
    phoneField.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence x,int a,int c,int f){} public void onTextChanged(CharSequence x,int a,int before,int count){updateNumberStatus();}public void afterTextChanged(Editable e){}});
    countryField.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> a){}public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){updateNumberStatus();}});
    form.addView(button(R.string.paste,()->{
      ClipboardManager c=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
      if(c!=null && c.hasPrimaryClip() && c.getPrimaryClip()!=null && c.getPrimaryClip().getItemCount()>0){
        CharSequence value=c.getPrimaryClip().getItemAt(0).coerceToText(this);
        Matcher m=Pattern.compile("(?:\\+|00)?[0-9][0-9 ()\\-.]{6,24}[0-9]").matcher(value);
        phoneField.setText(m.find()?m.group():value);phoneField.setSelection(phoneField.length());
      }
    }));
    LinearLayout messageCard=col();addCard(area,messageCard,8);
    messageCard.addView(text("✎  "+getString(R.string.message_optional),16,true));
    msgField=edit(msg,R.string.template_hint,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    msgField.setSingleLine(false);msgField.setMinLines(3);messageCard.addView(msgField);
    if(templates.length()>0)messageCard.addView(button(R.string.choose_template,()->{
      String[] names=new String[templates.length()];
      for(int i=0;i<names.length;i++)names[i]=templates.optJSONObject(i).optString("name");
      new AlertDialog.Builder(this).setItems(names,(d,which)->msgField.setText(templates.optJSONObject(which).optString("body"))).show();
    }));
    messageCard.addView(text(getString(R.string.chat_app_label),13,true));
    appField=spinner(new String[]{getString(R.string.automatic),getString(R.string.standard),getString(R.string.business)},selectedApp);messageCard.addView(appField);
    Button open=accentButton(R.string.open_chat,this::openChat);area.addView(open);
    hint(R.string.manual_send_note);
    LinearLayout links=row();
    links.addView(button(R.string.copy_link,()->{String u=link();if(u!=null){((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("chat link",u));Toast.makeText(this,R.string.copied,Toast.LENGTH_SHORT).show();}}),new LinearLayout.LayoutParams(0,-2,1));
    links.addView(button(R.string.share_link,()->{String u=link();if(u!=null){Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_TEXT,u);startActivity(Intent.createChooser(i,getString(R.string.share_link)));}}),new LinearLayout.LayoutParams(0,-2,1));
    area.addView(links);
    area.addView(button(R.string.favorite,this::addFavorite));hint(R.string.independent);
  }
  private void updateNumberStatus(){
    if(liveStatus==null || phoneField==null || countryField==null)return;
    String value=phoneField.getText().toString().trim();
    boolean ready=PhoneNumbers.normalize(value,codes[countryField.getSelectedItemPosition()])!=null;
    liveStatus.setText(value.isEmpty()?getString(R.string.number_empty):ready?"✓  "+getString(R.string.number_ready):getString(R.string.number_check));
    liveStatus.setTextColor(value.isEmpty()?MUTE:ready?GREEN:0xffbf5b2d);
  }
  private String digits(String s){return s.replaceAll("[^0-9]","");}
  private String e164(){
    if(phoneField==null || countryField==null)return null;
    return PhoneNumbers.normalize(phoneField.getText().toString(),codes[countryField.getSelectedItemPosition()]);
  }
  private String link(){
    String n=e164();if(n==null){phoneField.setError(getString(R.string.error_number));return null;}
    String m=msgField.getText().toString().trim();return "https://wa.me/"+n+(m.isEmpty()?"":"?text="+Uri.encode(m));
  }
  private void openChat(){
    String u=link();if(u==null)return;
    rememberFields();
    Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse(u));
    int mode=appField.getSelectedItemPosition();
    if(mode!=0)i.setPackage(mode==1?"com.whatsapp":"com.whatsapp.w4b");
    try{startActivity(i);saveHistory(e164(),msgField.getText().toString());}
    catch(Exception e){
      try{i.setPackage(null);startActivity(i);saveHistory(e164(),msgField.getText().toString());}
      catch(Exception ignored){Toast.makeText(this,R.string.error_open,Toast.LENGTH_SHORT).show();}
    }
  }
  private void saveHistory(String n,String m){
    JSONArray all=new JSONArray();
    try{JSONObject first=new JSONObject();first.put("number",n);first.put("message",m);all.put(first);
      for(int j=0;j<history.length() && all.length()<40;j++){JSONObject x=history.optJSONObject(j);if(x!=null && !n.equals(x.optString("number")))all.put(x);}
    }catch(Exception ignored){}
    history=all;save("history",history);
  }
  private void addFavorite(){
    String n=e164();if(n==null){phoneField.setError(getString(R.string.error_number));return;}
    EditText name=edit("",R.string.favorite_default,InputType.TYPE_CLASS_TEXT);
    new AlertDialog.Builder(this).setTitle(R.string.favorite_name).setView(name)
      .setNegativeButton(android.R.string.cancel,null).setPositiveButton(R.string.saved,(d,w)->{
        try{JSONArray all=new JSONArray();for(int j=0;j<favorites.length();j++){JSONObject x=favorites.optJSONObject(j);if(x!=null && !n.equals(x.optString("number")))all.put(x);}
          JSONObject obj=new JSONObject();obj.put("number",n);obj.put("name",name.getText().toString().trim().isEmpty()?getString(R.string.favorite_default):name.getText().toString().trim());
          all.put(obj);favorites=all;save("favorites",favorites);Toast.makeText(this,R.string.saved,Toast.LENGTH_SHORT).show();
        }catch(Exception ignored){}
      }).show();
  }
  private void drawRecent(){
    area.addView(text("★  "+getString(R.string.favorites),21,true));if(favorites.length()==0)emptyCard(R.string.empty_favorites);
    for(int i=0;i<favorites.length();i++){JSONObject x=favorites.optJSONObject(i);if(x!=null)entry(x.optString("name"),"+"+x.optString("number"),()->{phone="+"+x.optString("number");msg="";tab=0;draw();},()->{remove("favorites",x);draw();});}
    space(area,10);area.addView(text("◷  "+getString(R.string.history),21,true));if(history.length()==0)emptyCard(R.string.empty_history);
    for(int i=0;i<history.length();i++){JSONObject x=history.optJSONObject(i);if(x!=null)entry("+"+x.optString("number"),x.optString("message"),()->{phone="+"+x.optString("number");msg=x.optString("message");tab=0;draw();},()->{remove("history",x);draw();});}
    if(history.length()>0)area.addView(button(R.string.clear_history,()->confirmClear(false)));
  }
  private void emptyCard(int message){LinearLayout empty=col();addCard(area,empty,9);TextView symbol=text("✦",28,true);symbol.setTextColor(GREEN);symbol.setGravity(Gravity.CENTER);empty.addView(symbol);TextView desc=caption(getString(message));desc.setGravity(Gravity.CENTER);empty.addView(desc);}
  private void entry(String name,String sub,Runnable use,Runnable delete){
    LinearLayout item=col();item.setPadding(dp(14),dp(12),dp(14),dp(12));item.setBackground(outline(Color.WHITE,0xffe2edf2,18));item.setElevation(dp(2));
    TextView itemTitle=text(name,16,true);item.addView(itemTitle);TextView subtitle=text(sub,12,false);subtitle.setTextColor(MUTE);subtitle.setMaxLines(2);subtitle.setEllipsize(TextUtils.TruncateAt.END);item.addView(subtitle);
    LinearLayout tools=row();tools.addView(button(R.string.use,use),new LinearLayout.LayoutParams(0,-2,1));
    tools.addView(button(R.string.delete,delete),new LinearLayout.LayoutParams(0,-2,1));item.addView(tools);
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(10);area.addView(item,p);
  }
  private void remove(String key,JSONObject target){
    JSONArray arr="favorites".equals(key)?favorites:"history".equals(key)?history:templates;JSONArray next=new JSONArray();
    for(int i=0;i<arr.length();i++)if(arr.opt(i)!=target)next.put(arr.opt(i));
    if("favorites".equals(key))favorites=next;else if("history".equals(key))history=next;else templates=next;save(key,next);
  }
  private void drawTemplates(){
    area.addView(text("▤  "+getString(R.string.templates),21,true));hint(R.string.templates_intro);
    LinearLayout templateCard=col();addCard(area,templateCard,12);
    templateCard.addView(text(getString(R.string.template_title),14,true));
    EditText name=edit("",R.string.template_title,InputType.TYPE_CLASS_TEXT);templateCard.addView(name);
    templateCard.addView(text(getString(R.string.template_text),14,true));EditText body=edit("",R.string.template_hint,InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    body.setSingleLine(false);body.setMinLines(3);templateCard.addView(body);
    templateCard.addView(accentButton(R.string.add_template,()->{
      String n=name.getText().toString().trim(),v=body.getText().toString().trim();
      if(n.isEmpty()){name.setError(getString(R.string.template_title));return;}if(v.isEmpty()){body.setError(getString(R.string.template_text));return;}
      try{JSONObject x=new JSONObject();x.put("name",n);x.put("body",v);templates.put(x);save("templates",templates);draw();}
      catch(Exception ignored){}
    }));
    for(int i=0;i<templates.length();i++){JSONObject x=templates.optJSONObject(i);if(x!=null)entry(x.optString("name"),x.optString("body"),()->{msg=x.optString("body");tab=0;draw();},()->{remove("templates",x);draw();});}
  }
  private void drawSettings(){
    area.addView(text("🌐  "+getString(R.string.language_label),21,true));
    Spinner language=spinner(new String[]{getString(R.string.language_system),getString(R.string.language_es),getString(R.string.language_en)},getLanguageChoice());
    area.addView(language);
    area.addView(button(R.string.apply_language,()->{
      int selected=language.getSelectedItemPosition();
      String value=selected==1?"es":selected==2?"en":"system";
      getSharedPreferences("chat_facil_settings",MODE_PRIVATE).edit().putString("language",value).apply();
      recreate();
    }));
    space(area,12);area.addView(text("♢  "+getString(R.string.privacy),21,true));hint(R.string.privacy_copy);hint(R.string.independent);
    area.addView(button(R.string.privacy_policy,()->{
      try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://tommymsc.github.io/chat-facil-privacy.html")));}
      catch(Exception e){Toast.makeText(this,R.string.error_open,Toast.LENGTH_SHORT).show();}
    }));
    if(consent!=null && consent.getPrivacyOptionsRequirementStatus()==ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED)
      area.addView(button(R.string.privacy_options,()->UserMessagingPlatform.showPrivacyOptionsForm(this,error->{})));
    area.addView(button(R.string.clear_history,()->confirmClear(false)));
    area.addView(button(R.string.clear_all,()->confirmClear(true)));
    if(!BuildConfig.REAL_ADS)hint(R.string.ads_notice);
  }
  private int getLanguageChoice(){
    String current=getSharedPreferences("chat_facil_settings",MODE_PRIVATE).getString("language","system");
    return "es".equals(current)?1:"en".equals(current)?2:0;
  }
  private void confirmClear(boolean all){
    new AlertDialog.Builder(this).setMessage(R.string.confirm_delete).setNegativeButton(android.R.string.cancel,null)
      .setPositiveButton(R.string.delete,(d,w)->{
        history=new JSONArray();save("history",history);
        if(all){favorites=new JSONArray();templates=new JSONArray();save("favorites",favorites);save("templates",templates);}
        draw();
      }).show();
  }
  private void requestConsent(){
    consent=UserMessagingPlatform.getConsentInformation(this);
    consent.requestConsentInfoUpdate(this,new ConsentRequestParameters.Builder().build(),()->{
      UserMessagingPlatform.loadAndShowConsentFormIfRequired(this,error->{if(consent.canRequestAds())startAds();});
      if(consent.canRequestAds())startAds();
    },error->{if(consent.canRequestAds())startAds();});
  }
  private void startAds(){
    if(adsReady)return;adsReady=true;
    new Thread(()->MobileAds.initialize(this,info->runOnUiThread(this::showBanner))).start();
  }
  private void showBanner(){
    if(!adsReady || adsArea==null)return;
    if(banner!=null){if(banner.getParent() instanceof android.view.ViewGroup)((android.view.ViewGroup)banner.getParent()).removeView(banner);adsArea.addView(banner);return;}
    banner=new AdView(this);banner.setAdUnitId(BuildConfig.BANNER_ID);banner.setAdSize(AdSize.BANNER);adsArea.addView(banner);banner.loadAd(new AdRequest.Builder().build());
  }
  @Override protected void onPause(){if(banner!=null)banner.pause();super.onPause();}
  @Override protected void onResume(){super.onResume();if(banner!=null)banner.resume();}
  @Override protected void onDestroy(){if(banner!=null){banner.destroy();banner=null;}super.onDestroy();}
}