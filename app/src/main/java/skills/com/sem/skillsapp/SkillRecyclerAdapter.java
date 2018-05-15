package skills.com.sem.skillsapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SkillRecyclerAdapter extends RecyclerView.Adapter<SkillRecyclerAdapter.ViewHolder> {

    public List<SkillPost> skill_list;

    public SkillRecyclerAdapter(List<SkillPost> skill_list) {

        this.skill_list = skill_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String desc_data = skill_list.get(position).getDesc();
        holder.setDescText(desc_data);

    }

    @Override
    public int getItemCount() {
        return skill_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.skill_desc);
            descView.setText(descText);

        }
    }

}
