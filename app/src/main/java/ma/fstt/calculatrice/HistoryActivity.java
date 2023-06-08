package ma.fstt.calculatrice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> historyOperation;
    private HistoryAdapter historyAdapter;

    private Button backToMainButton;
    private Button clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyOperation = getIntent().getStringArrayListExtra("historyOperation");

        historyAdapter = new HistoryAdapter(historyOperation);
        recyclerView.setAdapter(historyAdapter);
        Log.d("HistoryActivity", "History Operation List: " + historyOperation.toString());

        backToMainButton = findViewById(R.id.backToMainButton);
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainActivity();
            }
        });

        clearHistoryButton = findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearHistory() {
        historyOperation.clear();
        historyAdapter.notifyDataSetChanged();
    }
}
