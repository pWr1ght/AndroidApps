package com.example.yelptutorial;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;


import kotlin.jvm.Synchronized;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;
    Call<SearchResponse> call;
    List<Restaurant> restaurantList;
    OkHttpClient client;
    List<Restaurant> mRestaurant;
    ImageView mainImage;
    ProgressBar loading;
    TextView title;
    TextView message;
    Map<String, String> params;
    Button button;
    int Loc;



    int check = 0;

    int i;
    int iLast;
    boolean wait = false;
    int rCount = 50;
    int count = 0;
    List<Restaurant> rs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        mainImage = (ImageView) findViewById(R.id.mainImage);
        title = (TextView) findViewById(R.id.resTitle);
        message = (TextView) findViewById(R.id.rateCategory);
        mRestaurant = new ArrayList<>();
        Loc = 0;
        i=0;
        iLast = 0;
        loading = findViewById(R.id.progressBar);


//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

//        Picasso.get()
//                .load("https://i.imgur.com/DvpvklR.png")
//                .into(mainImage);


        mainImage.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                sameRestaurantNewPic();
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                lastRestaurant();
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                newRestuarant();
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                sameRestaurantPrevPic();
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });


        params = new HashMap<>();

// general params
        params.put("term", "food");
//        params.put("latitude", "40.581140");
//        params.put("longitude", "-111.914184");
        params.put("limit", "50");


        apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        call = yelpFusionApi.getBusinessSearch(params);
        new fetchPictures().execute("0");
        waitRestaurant(true);
    }

    private void sameRestaurantPrevPic() {
        Restaurant r = mRestaurant.get(i);
        if (r.getCurPic() > 0) {
            r.decCurPic();
            waitRestaurant(true);
        }
    }

    private void sameRestaurantNewPic() {
        Restaurant r = mRestaurant.get(i);
        if(r.getPictures().size() - 1 > r.getCurPic()) {
            r.incCurPic();
            waitRestaurant(true);
            if(r.getCurPic() - r.getiLast() > 5 && r.getPictures().size() - r.getCurPic() < 7) {
                r.setiLast(r.getCurPic());
                // download more pictures
            }
        }

    }

    private void lastRestaurant() {
        if(i > 0) {
            i--;
            waitRestaurant(true);
        }
    }
    
    private void newRestuarant() {
        // check if room
        // 4 restaurants
        // i -> 3
        if (mRestaurant.size()-1 > i) {
            i++;
            waitRestaurant(true);
            if(i - iLast > 5 && mRestaurant.size() - i < 7) {
                //Load more restaurants
                new fetchPictures().execute(String.valueOf(rCount));
                rCount += 50;
            }
        }
    }


    synchronized public void waitRestaurant(boolean client) {
        if(client) {
            if(mRestaurant.size() > i && mRestaurant.get(i).getPictures().size() > mRestaurant.get(i).getCurPic()) {
                restaurantCallBack();

            } else {
                wait = true;
                loading.setVisibility(View.VISIBLE);
            }
        } else {
            if(wait) {
                restaurantCallBack();
                wait= false;
                loading.setVisibility(View.INVISIBLE);
            }
        }
    }





    public void restaurantCallBack()
    {
        displayRestaurant(mRestaurant.get(i));
    }



    public void imageOn(View view) {
        Log.i("number", String.valueOf(i));

        Log.i("list of pictures", String.valueOf(rs.get(i).getPictures()));
    }


    public void displayRestaurant(Restaurant r) {
        for (int d = 0; d < mRestaurant.size(); d++) {
            if (mRestaurant.contains(r)) {
                check++;
            }
            if (check == 2) {
                Toast.makeText(this, "there is already a restaurant like this", Toast.LENGTH_SHORT).show();
            }
        }
        rs.add(r);
        button = (Button) findViewById(R.id.button1);
        count++;
        Picasso.get()
                .load(r.getPictures().get(r.getCurPic()))
                .into(mainImage);
        title.setText(r.getName());
        message.setText(r.getMessage());

        Log.i("Restuarants", r.getName() + " " + r.getMessage() + " " + count);

    }


    class fetchPictures extends AsyncTask<String, Restaurant, String> {
        Response<SearchResponse> response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Restaurant... values) {
            super.onProgressUpdate((values));
            mRestaurant.add(values[0]);
//            Log.i("mRestaurant", String.valueOf(mRestaurant.get(size).getName() + " " + mRestaurant.get(size).getPictures().size()));
            waitRestaurant(false);
        }

        @Override
        protected String doInBackground(String... mparams) {
            params.put("latitude", "40.581140");
            params.put("longitude", "-111.914184");
            params.put("offset", mparams[0]);
            call = yelpFusionApi.getBusinessSearch(params);
            try {
                //clone() if does not work
                response = call.clone().execute();
            }  catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (response != null) {
                List<Business> businessList = response.body().getBusinesses();
                restaurantList = new ArrayList<>();
                int pos = 0;
                Restaurant restaurant;
                for(Business business: businessList) {
                    restaurant = new Restaurant(business.getName(), business.getUrl());
                    restaurant.setMessage(business.getRating() + " " + Restaurant.convertCatToString(business.getCategories()));
                    restaurantList.add(restaurant);
                    getPics(restaurant, pos);
//                    Log.i("got restaurant", "d");


                    pos++;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        public void getPics(final Restaurant url, final int pos) {
        Request request = new Request.Builder()
                .url(url.getPicUrl())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                List<String> pictures = parseRestaurant.parsePicture(url.getPicUrl());
                if(pictures.size() > 0) {
                    restaurantList.get(pos).setPictures(pictures);
//                    Log.i("get pic", String.valueOf(restaurantList.get(pos)) + " " + pos);

                    publishProgress(restaurantList.get(pos));

                }

            }
        });
        }
    }

}



//package com.example.yelptutorial;
//
//        import androidx.annotation.MainThread;
//        import androidx.appcompat.app.AppCompatActivity;
//
//        import android.os.AsyncTask;
//        import android.os.Bundle;
//        import android.os.StrictMode;
//        import android.util.Log;
//        import android.view.MotionEvent;
//        import android.view.View;
//        import android.widget.ImageView;
//        import android.widget.ProgressBar;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.squareup.picasso.Picasso;
//        import com.yelp.fusion.client.connection.YelpFusionApi;
//        import com.yelp.fusion.client.connection.YelpFusionApiFactory;
//        import com.yelp.fusion.client.models.Business;
//        import com.yelp.fusion.client.models.Category;
//        import com.yelp.fusion.client.models.SearchResponse;
//
//        import org.jetbrains.annotations.NotNull;
//
//        import java.io.IOException;
//        import java.util.ArrayList;
//        import java.util.Arrays;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.Map;
//        import java.util.concurrent.SynchronousQueue;
//
//
//        import kotlin.jvm.Synchronized;
//        import okhttp3.Callback;
//        import okhttp3.OkHttpClient;
//        import okhttp3.Request;
//        import retrofit2.Call;
//        import retrofit2.Response;
//
//public class MainActivity extends AppCompatActivity {
//    YelpFusionApiFactory apiFactory;
//    YelpFusionApi yelpFusionApi;
//    Call<SearchResponse> call;
//    List<Restaurant> restaurantList;
//    OkHttpClient client;
//    List<Restaurant> mRestaurant;
//    ImageView mainImage;
//    ProgressBar loading;
//    TextView title;
//    TextView message;
//    Map<String, String> params;
//
//
//    int i;
//    int rLast;
//    boolean wait = false;
//    int rCount = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        client = new OkHttpClient();
//        mainImage = (ImageView) findViewById(R.id.mainImage);
//        title = (TextView) findViewById(R.id.resTitle);
//        message = (TextView) findViewById(R.id.rateCategory);
//        mRestaurant = new ArrayList<>();
//        i=0;
//        loading = findViewById(R.id.progressBar);
////        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
////        StrictMode.setThreadPolicy(policy);
//
////        Picasso.get()
////                .load("https://i.imgur.com/DvpvklR.png")
////                .into(mainImage);
//
//
//        mainImage.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
//            public void onSwipeTop() {
//                sameRestForwardPic();
//                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSwipeRight() {
//                prevRest();
//                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSwipeLeft() {
//                newRest();
//                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSwipeBottom() {
//                sameRestPrevPic();
//                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//
//        params = new HashMap<>();
//
//// general params
////        params.put("term", "French");
//        params.put("latitude", "40.581140");
//        params.put("longitude", "-111.914184");
//        params.put("limit", "40");
//
//
//        apiFactory = new YelpFusionApiFactory();
//        try {
//            yelpFusionApi = apiFactory.createAPI("mvYspiEoknWGfBtkCmd_p8pzmjF_yJSjjJba7xit0kE9c6BGvzeogHrGp6htf-4KyFqvCb0W2sPu69D0WD8zDWY8jO64mtRqCY76e6OUBs_CEunuep9FrjcvQu9RXXYx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        call = yelpFusionApi.getBusinessSearch(params);
//        new fetchPictures().execute("0");
//        Log.i("after fetch", "fetch");
//        waitRestaurant(true);
//    }
//
//
//
//
//
//    private void sameRestPrevPic() {
//        Restaurant r = mRestaurant.get(i);
//        if(r.getCurPic() >0) {
//            r.decCurPic();
//            waitRestaurant(true);
//        }
//    }
//
//    private void prevRest() {
//        if(i > 0) {
//            i--;
//            waitRestaurant(true);
//
//        }
//    }
//
//    private void newRest() {
//        if (mRestaurant.size()-1 >= i) {
//            i++;
//            waitRestaurant(true);
//            if(i - rLast > 50 && mRestaurant.size() - 1 > 40) {
////                fetchPictures f = new fetchPictures();
////                f.execute("10");
//                new fetchPictures().execute("40");
//                rCount += 40;
//            }
//        }
//    }
//
//    private void sameRestForwardPic() {
//        Restaurant r = mRestaurant.get(i);
//        if (r.getPictures().size() -1 > r.getCurPic()) {
//            r.incCurPic();
//            waitRestaurant(true);
//            if(r.getCurPic() - r.getiLast() > 20 && r.getPictures().size() - r.getCurPic() < 20) {
//                r.setiLast(r.getCurPic());
//            }
//        }
//    }
//
//    synchronized public void waitRestaurant(boolean client) {
//        if(client) {
//            if(mRestaurant.size() > i && mRestaurant.get(i).getPictures().size() > mRestaurant.get(i).getCurPic()) {
//                Log.i("client", "post new pic");
//                restaurantCallBack();
//
//            } else {
//                Log.i("waiting", "waiting");
//                wait = true;
//                loading.setVisibility(View.VISIBLE);
//            }
//        } else {
//            if(wait) {
//                Log.i("processs", "calllllllllllback");
//                restaurantCallBack();
//                wait= false;
//                loading.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//
//
//
//
//    public void restaurantCallBack()
//    {
//        displayRestaurant(mRestaurant.get(i));
//    }
//
//
//
//
//
//
//    public void displayRestaurant(Restaurant r) {
//        Log.i("f,", "it is going");
//        Picasso.get()
//                .load(r.getPictures().get(r.getCurPic()))
//                .into(mainImage);
//        title.setText(r.getName());
//        message.setText(r.getMessage());
//
//    }
//
//
//    class fetchPictures extends AsyncTask<String, Restaurant, String> {
//        Response<SearchResponse> response = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Restaurant... values) {
//            super.onProgressUpdate((values));
//            Log.i("Progress update", Arrays.toString(values));
//            mRestaurant.add(values[0]);
//            int size = mRestaurant.size()-1;
//            Log.i("mRestaurant", String.valueOf(mRestaurant.get(size).getName() + " " + mRestaurant.get(size).getPictures().size()));
//            waitRestaurant(false);
//        }
//
//        @Override
//        protected String doInBackground(String... mparams) {
//
//            params.put("offset", mparams[0]);
//            try {
//                response = call.execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (response != null) {
//                List<Business> businessList = response.body().getBusinesses();
//                restaurantList = new ArrayList<>();
//                Restaurant restaurant;
//                int pos = 0;
//                for(Business business: businessList) {
//                    restaurant = new Restaurant(business.getName(), business.getUrl());
//                    restaurant.setMessage(business.getRating() + " " + Restaurant.convertCatToString(business.getCategories()));
//                    restaurantList.add(restaurant);
//                    getPics(restaurant.getPicUrl(), pos);
////                    Log.i("got restaurant", "d");
//
//
//                    pos++;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            Log.i("finsished", "finished");
//            super.onPostExecute(s);
//        }
//
//        public void getPics(String url, final int pos) {
//            final String Url = url;
//            Request request = new Request.Builder()
//                    .url(Url)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
//                    List<String> pictures = parseRestaurant.parsePicture(Url);
//                    if(pictures.size() > 0) {
//                        restaurantList.get(pos).setPictures(pictures);
////                    Log.i("get pic", String.valueOf(restaurantList.get(pos)) + " " + pos);
//                        publishProgress(restaurantList.get(pos));
//
//                    }
//
//                }
//            });
//        }
//    }
//
//}
