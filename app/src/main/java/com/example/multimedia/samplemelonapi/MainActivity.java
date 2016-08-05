package com.example.multimedia.samplemelonapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View Intitialize
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); // text 하나만 들어갈 수 있다.
        listView.setAdapter(mAdapter);



        // API 연결 Task 실행 _ doInBackground() 실행되겠지..
        new MelonTask().execute(1, 50);

    }


    public static final String MELON_URL = "http://apis.skplanetx.com/melon/charts/realtime?version=1&page=%d&count=%d";

    public class MelonTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            // 접속... Melon API
            int page = params[0];
            int count = params[1];
            String urlText = String.format(MELON_URL, page, count);

            try {
                URL url = new URL(urlText);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 입력한 url을 conn 이라는 커넥션 객체로 형변환
                // conn 에 내 앱 KEY나 거기에 필요한 정보를 저장할 것임
                // KEY value 형식으로 속성을 정할 수 있다. 지정된 이름으로 보내야한다.
                // 이러한 App Key 인증은 홈페이지에 나와있다!!
                conn.setRequestProperty("AppKey", "e22fca12-d5a7-360d-adc1-17a35fe620de");

                // 404나 그런걸 http response code 라고 한다. (자세한건 구글 검색ㄱㄱ)
                // 성공을 하던 실패를 하던 response가 code를 보낸다. 그걸 아래에 code에 저장
                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    // 연결 성공!
                    // responseString을 받고,
                    // (BufferedReader는 읽어오는 것. conn에서 받아온 stream을 받아서 inputStreamReader가 읽는다.)
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    // String이 올 수도 있지만 String 형태의 파일이 올 수도 있으므로 BufferedReader를 쓰는 것이다!
                    StringBuilder sb = new StringBuilder();

                    String line; // for문의 i 같은 놈이다
                    // br에 있는 버퍼스트림을 줄 단위로 분석을 할 건데 그게 이 아래 while문이다.
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n\r"); // 라인이 null이 아니면 string builder에 추가한 뒤에 줄바꿈한다.
                    }
                    return sb.toString();

                    // 쓰기 좋게 Parsing 하자 >> onPostExecute(String s)   // 리턴된 responseString이 들어감

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null; // null이 왔다는 것은 연결 실패라는 것!! 성공시에만 string을 building 하는 거니까!
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            // MelonTask가 execute된 이후에 실행되는 callback method
            // responseString 만 나오게 하면 뭔가 쫘르르르르르르륵 길게 나온다 그걸 아래에서 파싱해서 보기 좋게 만들자

            // Parsing을 여기서!!
            Gson gson = new Gson();
            // 파싱한 결과를 최상위 객체에다가 넣는다. 그러면 구조화된게 딱 딱 들어가니까!
            MelonResult result = gson.fromJson(responseString, MelonResult.class);

            /*
            배열을 순회하는 for 문
             Song song = i  대신 객체일 뿐... song 배열을 순회한다.
            song배열은 result.melon.songs.song !
            result.melon.songs.song배열의 크기만큼 순회한다.
            */

            for(Song song : result.melon.songs.song) {
                mAdapter.add(song.songName);
            }

        }

    }

}
