package bf.uv.eventconnect;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventModel> eventList;

    public EventAdapter(List<EventModel> eventList) {
        this.eventList = eventList;
    }

    public void setEvents(List<EventModel> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titre, date, description, lieu;
        ImageView imageEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.textTitle);
            date = itemView.findViewById(R.id.textDate);
            description = itemView.findViewById(R.id.textDescription);
            lieu = itemView.findViewById(R.id.textLieu);
            imageEvent = itemView.findViewById(R.id.imageEvent);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);

        //Texte
        holder.titre.setText(event.getTitre());
        holder.date.setText(event.getDate());
        holder.description.setText(event.getDescription());
        holder.lieu.setText(event.getLieu());

        // Image (si disponible en base)
        if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
            holder.imageEvent.setImageURI(Uri.fromFile(new File(event.getImagePath())));
        } else {
            holder.imageEvent.setImageResource(R.drawable.bg2); // image par défaut
        }

        // Click -> ouvre DetailActivity avec extras
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("id", event.getId());
            intent.putExtra("titre", event.getTitre());
            intent.putExtra("date", event.getDate());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("lieu", event.getLieu());
            intent.putExtra("imagePath", event.getImagePath());
            intent.putExtra("category", event.getCategory());
            intent.putExtra("organisateur", event.getOrganisateur());
            intent.putExtra("latitude", event.getLatitude());
            intent.putExtra("longitude", event.getLongitude());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }
}
