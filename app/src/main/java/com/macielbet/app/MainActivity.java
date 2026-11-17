package com.macielbet.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Random;

public class MainActivity extends Activity {
    LinearLayout root, content;
    TextView balanceLabel, resultLabel;
    SharedPreferences prefs;
    Random rng = new Random();
    int balance, stake = 100;
    String game = "";
    final int GOLD = Color.rgb(255,195,65), DARK = Color.rgb(13,17,34), PANEL = Color.rgb(27,34,57);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("maciel_offline", MODE_PRIVATE);
        balance = prefs.getInt("balance",10000);
        home();
    }
    GradientDrawable bg(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    TextView text(String s, int size, int color) { TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setPadding(12,18,12,18);return t; }
    void addSpace(int h) { View v = new View(this);content.addView(v,new LinearLayout.LayoutParams(1,h)); }
    void button(String s, Runnable action) {
        TextView b=text(s,19,Color.WHITE);b.setTypeface(null,Typeface.BOLD);b.setBackground(bg(PANEL,24));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,7,0,7);content.addView(b,p);
        b.setOnClickListener(v->action.run());
    }
    void base(String title) {
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(DARK);
        TextView brand=text("◆ MACIEL BET ◆",27,GOLD);brand.setTypeface(null,Typeface.BOLD);root.addView(brand);
        balanceLabel=text("SALDO VIRTUAL: "+balance+" moedas",17,Color.WHITE);root.addView(balanceLabel);
        ScrollView sc=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(22,10,22,30);sc.addView(content);root.addView(sc);setContentView(root);
        TextView heading=text(title,23,GOLD);heading.setTypeface(null,Typeface.BOLD);content.addView(heading);
    }
    void home() {
        game="";base("ESCOLHA SEU JOGO");
        content.addView(text("Diversão offline • moedas fictícias • sem prêmios reais",14,0xffb9c1d6));
        button("🎰  CAÇA-NÍQUEL",()->open("CAÇA-NÍQUEL"));
        button("🐉  DRAGÃO DOURADO",()->open("DRAGÃO DOURADO"));
        button("🐯  TIGRE DA SORTE",()->open("TIGRE DA SORTE"));
        button("🎡  ROLETA DA SORTE",()->open("ROLETA DA SORTE"));
        button("🪙  CARA OU COROA",()->open("CARA OU COROA"));
        button("🎲  DADO PREMIADO",()->open("DADO PREMIADO"));
        button("↻  REINICIAR MOEDAS VIRTUAIS",()->{
            balance=10000;save();home();Toast.makeText(this,"Saldo reiniciado",Toast.LENGTH_SHORT).show();
        });
    }
    void open(String selected) {
        game=selected;base(game);
        content.addView(text("Escolha o valor da rodada:",17,Color.WHITE));
        LinearLayout choices=new LinearLayout(this);choices.setGravity(Gravity.CENTER);
        for(int amount:new int[]{10,50,100,500}) {
            TextView b=text(""+amount,16,GOLD);b.setBackground(bg(PANEL,15));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(3,3,3,3);choices.addView(b,lp);
            b.setOnClickListener(v->{stake=amount;Toast.makeText(this,"Rodada: "+stake+" moedas",Toast.LENGTH_SHORT).show();});
        }
        content.addView(choices);
        resultLabel=text("Valor atual: "+stake+" moedas\nToque em JOGAR!",24,Color.WHITE);
        resultLabel.setMinHeight(210);resultLabel.setBackground(bg(PANEL,24));content.addView(resultLabel);
        addSpace(16);
        button("▶ JOGAR • "+stake+" moedas",()->play());
        button("← VOLTAR AO MENU",()->home());
    }
    void play() {
        if(balance<stake){Toast.makeText(this,"Saldo insuficiente. Reinicie as moedas no menu.",Toast.LENGTH_LONG).show();return;}
        balance-=stake;
        int prize=0;String message="";
        if(game.equals("CAÇA-NÍQUEL")||game.equals("DRAGÃO DOURADO")||game.equals("TIGRE DA SORTE")) {
            String[] icons=game.equals("DRAGÃO DOURADO")?new String[]{"🐉","💎","⭐","🔥","👑"}:game.equals("TIGRE DA SORTE")?new String[]{"🐯","🍀","💰","⭐","👑"}:new String[]{"🍒","🍋","🔔","💎","7️⃣"};
            int a=rng.nextInt(5),b=rng.nextInt(5),c=rng.nextInt(5);
            if(a==b&&b==c)prize=stake*8;else if(a==b||a==c||b==c)prize=stake*2;
            message=icons[a]+"  "+icons[b]+"  "+icons[c];
        } else if(game.equals("ROLETA DA SORTE")) {
            int[] mult={0,0,0,1,1,2,3,5};int n=rng.nextInt(mult.length);prize=stake*mult[n];message="🎡  Multiplicador: "+mult[n]+"x";
        } else if(game.equals("CARA OU COROA")) {
            boolean heads=rng.nextBoolean();prize=heads?stake*2:0;message=heads?"🪙 CARA!":"🪙 COROA!";
        } else {
            int n=1+rng.nextInt(6);prize=n>=5?stake*3:0;message="🎲 Saiu o número "+n;
        }
        balance+=prize;save();balanceLabel.setText("SALDO VIRTUAL: "+balance+" moedas");
        resultLabel.setText(message+"\n\n"+(prize>0?"PRÊMIO: +"+prize+" moedas":"SEM PRÊMIO NESTA RODADA")+"\nSaldo: "+balance);
    }
    void save(){prefs.edit().putInt("balance",balance).apply();}
    @Override public void onBackPressed(){home();}
}
