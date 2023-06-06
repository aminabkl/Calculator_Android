package ma.fstt.calculatrice;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = getIntent().getStringArrayListExtra("historyList");

        HistoryAdapter adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);
        Log.d("HistoryActivity", "History List: " + historyList.toString());

    }
}
