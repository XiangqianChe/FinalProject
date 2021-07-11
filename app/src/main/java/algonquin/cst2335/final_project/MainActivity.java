package algonquin.cst2335.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button DiYuBtn = findViewById(R.id.DiYu);
        Button XiangqianCheBtn = findViewById(R.id.XiangqianChe);
        Button ZheWangBtn = findViewById(R.id.ZheWang);
        Button ZhiqianQuBtn = findViewById(R.id.ZhiqianQu);

        DiYuBtn.setOnClickListener( v -> {
            startActivity(new Intent(MainActivity.this, DiYu.class));
        } );
        XiangqianCheBtn.setOnClickListener( v -> {
            startActivity(new Intent(MainActivity.this, XiangqianChe.class));
        } );
        ZheWangBtn.setOnClickListener( v -> {
            startActivity(new Intent(MainActivity.this, ZheWang.class));
        } );
        ZhiqianQuBtn.setOnClickListener( v -> {
            startActivity(new Intent(MainActivity.this, ZhiqianQu.class));
        } );
    }
}