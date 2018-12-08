package com.youcmt.youdmcapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.youcmt.youdmcapp.model.UpdateProfileRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.BASE_AVATAR_URL;
import static com.youcmt.youdmcapp.Constants.PROFILE_IMG;

/**
 * Created by Stanislav Ostrovskii on 11/22/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */
public class AvatarFragment extends Fragment {
    private static final String TAG = "AvatarFragment";
    private RecyclerView mRecyclerView;
    private AvatarAdapter mAdapter;
    private List<String> mAvatarUrls;
    private SharedPreferences mPreferences;
    private int mCurrentImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getActivity()
                .getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        mCurrentImage = mPreferences.getInt(PROFILE_IMG, 0);
        getAvatarUrls();
    }

    private void getAvatarUrls() {
        mAvatarUrls = new ArrayList<>(50);
        mAvatarUrls.add(BASE_AVATAR_URL + 0 + ".png");
        for(int i=101; i<151; i++)
        {
            mAvatarUrls.add(BASE_AVATAR_URL + i + ".png");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        mRecyclerView = view.findViewById(R.id.avatar_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if(isAdded())
        {
            mAdapter = new AvatarAdapter(mAvatarUrls);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class AvatarHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;
        private int mImageNumber;

        public AvatarHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.avatar_image_view);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCurrentImage!=mImageNumber)
                    {
                        confirmDialog(mImageNumber);
                    }
                }
            });
        }
        public void bindDrawable(Drawable drawable, String url)
        {
            Picasso.with(getContext()).load(url).placeholder(drawable).into(mImageView);
            //attempts to retrieve number from URL
            url = url.replaceAll("[^0-9]", "");
            mImageNumber = Integer.valueOf(url);
            if(mImageNumber==mCurrentImage) {
                setHighlighted(true);
            }
            else setHighlighted(false);
        }

        public void setHighlighted(boolean highlight)
        {
            if(highlight)
            {
                mImageView.setBackground(getResources().getDrawable(R.drawable.avatar_selected_background));
            }
            else
            {
                mImageView.setBackground(null);
            }
        }
    }

    private void confirmDialog(final int currentImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.change_avatar_prompt);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPreferences.edit().putInt(PROFILE_IMG, currentImage).apply();
                mCurrentImage = currentImage;
                updateProfileImg();
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void updateProfileImg() {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        UpdateProfileRequest request  = new UpdateProfileRequest();
        request.setNew_profile_img(String.valueOf(mCurrentImage));

        Call<ResponseBody> call = client.updateProfile(getAuthHeader(), request, Constants.jsonHeader());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200)
                {
                    Toast.makeText(getActivity(), "Avatar changed", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());

                        //the API seems to be inconsistent with error messages.
                        //so we try to extract both "msg" and "message"
                        //and give up only if both are null
                        String errorString = errorMessage.getString("msg");
                        if(errorString==null) errorString = errorMessage.getString("message");

                        errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
                        if(errorString.equalsIgnoreCase("Fresh token required"))
                        {
                            Toast.makeText(getActivity(), R.string.login_again, Toast.LENGTH_SHORT).show();
                            ((AccountActivity) getActivity()).logoutUser();
                        }
                        else Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        displayUnknownError();
                        e.printStackTrace();
                    } catch (JSONException j)
                    {
                        displayUnknownError();
                        j.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @NonNull
    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    private class AvatarAdapter extends RecyclerView.Adapter<AvatarHolder>
    {
        private List<String> mAvatarUrls;

        public AvatarAdapter(List<String> urls)
        {
            mAvatarUrls = urls;
        }

        @Override
        public AvatarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.avatar_item, parent, false);
            return new AvatarHolder(view);
        }

        @Override
        public void onBindViewHolder(AvatarHolder holder, int position) {
            String url = mAvatarUrls.get(position);
            Drawable drawable = getResources().getDrawable(R.drawable.default_avatar);
            holder.bindDrawable(drawable, url);
        }

        @Override
        public int getItemCount() {
            return mAvatarUrls.size();
        }
    }
}
