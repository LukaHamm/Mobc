package app.thecity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import app.thecity.R;
import app.thecity.model.Evaluation;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.EvaluationsViewHolder> {

    private List<Evaluation> evaluationList;
    private Context context;

    public AdapterComments(Context context, List<Evaluation> evaluationList) {
        this.context = context;
        this.evaluationList=evaluationList;
    }

    /**
     * Bläst das Layout für einen Kommentar auf
     * @param parent   Die ViewGroup in der das Element eingesetzt werden soll
     * @param viewType Der Typ der View .
     * @return
     */
    @NonNull
    @Override
    public EvaluationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new EvaluationsViewHolder(view);
    }

    /**
     * Setzt den Kommentartext und Verfasser
     * @param holder   Die Viewholder-Komponente die das Layout beinhaltet
     * @param position Die Position der Daten.
     */
    @Override
    public void onBindViewHolder(@NonNull EvaluationsViewHolder holder, int position) {
        Evaluation comment = evaluationList.get(position);
        holder.commentText.setText(comment.text);
        holder.usernameText.setText(comment.username);
    }

    @Override
    public int getItemCount() {
            return evaluationList.size();

    }


    /**
     * Unterklasse um die Kommentare in den Veiwholdern darstellen zu können
     */
    public class EvaluationsViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView commentText;

        public EvaluationsViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user);
            commentText = itemView.findViewById(R.id.evaluationText);
        }
    }

    /**
     * Einfügen der neu gefetchten Kommentare
     * @param evaluationList
     */
    public void insertData(List<Evaluation> evaluationList){
        this.evaluationList = evaluationList;
        notifyDataSetChanged();

    }
}
