package ma.fstt.calculatrice;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<String> historyOperation;

    public HistoryAdapter(List<String> historyOperation) {
        this.historyOperation = historyOperation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String operation = historyOperation.get(position);
        holder.operationTextView.setText(operation);
        Log.d("HistoryAdapter", "Operation: " + operation);

    }

    @Override
    public int getItemCount() {
        return historyOperation.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView operationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            operationTextView = itemView.findViewById(R.id.operationTextView);
        }
    }
}
