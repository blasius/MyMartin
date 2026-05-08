package rw.martinhardware.mymartin.ui.compliance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.ui.compliance.ComplianceViewModel.Fine;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComplianceAdapter extends RecyclerView.Adapter<ComplianceAdapter.FineViewHolder> {
    
    private List<Fine> fines = new ArrayList<>();

    @NonNull
    @Override
    public FineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fine, parent, false);
        return new FineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineViewHolder holder, int position) {
        Fine currentFine = fines.get(position);
        holder.bind(currentFine);
    }

    @Override
    public int getItemCount() {
        return fines.size();
    }

    public void setFines(List<Fine> fines) {
        this.fines = fines;
        notifyDataSetChanged();
    }

    class FineViewHolder extends RecyclerView.ViewHolder {
        private TextView textFineId;
        private TextView textFineType;
        private TextView textFineDate;
        private TextView textFineAmount;
        private TextView textFineStatus;

        public FineViewHolder(@NonNull View itemView) {
            super(itemView);
            textFineId = itemView.findViewById(R.id.text_fine_id);
            textFineType = itemView.findViewById(R.id.text_fine_type);
            textFineDate = itemView.findViewById(R.id.text_fine_date);
            textFineAmount = itemView.findViewById(R.id.text_fine_amount);
            textFineStatus = itemView.findViewById(R.id.text_fine_status);
        }

        public void bind(Fine fine) {
            textFineId.setText("Fine #" + fine.getId());
            textFineType.setText(fine.getType());
            textFineDate.setText(fine.getDate());
            
            // Format amount as currency
            NumberFormat formatter = NumberFormat.getInstance(Locale.US);
            textFineAmount.setText("RWF " + formatter.format(fine.getAmount()));
            
            textFineStatus.setText(fine.getStatus());

            // Set status color
            switch (fine.getStatus()) {
                case "PAID":
                    textFineStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "UNPAID":
                    textFineStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    textFineStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
}
