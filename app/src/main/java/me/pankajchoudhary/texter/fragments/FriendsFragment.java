/*
 * Copyright (c) 2021.
 *
 * This is a part of Texter Project (https://github.com/pannkajj/Texter/)
 */

package me.pankajchoudhary.texter.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.pankajchoudhary.texter.R;
import me.pankajchoudhary.texter.activities.ChatActivity;
import me.pankajchoudhary.texter.activities.ProfileActivity;
import me.pankajchoudhary.texter.holders.FriendHolder;
import me.pankajchoudhary.texter.models.Friend;


public class FriendsFragment extends Fragment {
    private FirebaseRecyclerAdapter adapter;

    public FriendsFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initializing Friends database

        DatabaseReference friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        friendsDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        final RecyclerView recyclerView = view.findViewById(R.id.friends_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Friend> options = new FirebaseRecyclerOptions.Builder<Friend>().setQuery(friendsDatabase.orderByChild("date"), Friend.class).build();

        adapter = new FirebaseRecyclerAdapter<Friend, FriendHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final FriendHolder holder, int position, final Friend model)
            {
                final String userid = getRef(position).getKey();

                holder.setHolder(userid, model.getDate());
                holder.getView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        PopupMenu popup = new PopupMenu(getContext(), view);

                        popup.getMenu().add(Menu.NONE, 1, 1, "View Profile");
                        popup.getMenu().add(Menu.NONE, 2, 2, "Send Message");

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                        {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem)
                            {
                                switch(menuItem.getItemId())
                                {
                                    case 1:
                                        Intent userProfileIntent = new Intent(getContext(), ProfileActivity.class);
                                        userProfileIntent.putExtra("userid", userid);
                                        startActivity(userProfileIntent);
                                        return true;
                                    case 2:
                                        Intent sendMessageIntent = new Intent(getContext(), ChatActivity.class);
                                        sendMessageIntent.putExtra("userid", userid);
                                        startActivity(sendMessageIntent);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }

            @Override
            public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new FriendHolder(getActivity(), view, getContext());
            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();

                TextView text = view.findViewById(R.id.f_friends_text);

                if(adapter.getItemCount() == 0)
                {
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                }

                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        };

        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onStart()
    {
        super.onStart();

        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }
}