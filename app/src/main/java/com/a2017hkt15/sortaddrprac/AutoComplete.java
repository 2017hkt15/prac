package com.a2017hkt15.sortaddrprac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class AutoComplete extends AppCompatActivity {
    TMapData tMapdata = new TMapData();
    String address_send;
    static double lat;
    static double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);
        Button button = (Button) findViewById(R.id.button);

        TMapView tmapview = new TMapView(this);
        tmapview.setSKPMapApiKey("d78cbfb1-f9ee-3742-af96-bf845debb9ab");
    }

    public void onClick(View v) {

        final ArrayList<String> addressList = new ArrayList<>();
        String address;
        final EditText editText = (EditText) findViewById(R.id.edit);
        address = editText.getText().toString();

        //address가 장소 이름 edittext로 부터 받아옴
        //address를 키워드로 해당 리스트를 받아옴
        tMapdata.autoComplete(address, new TMapData.AutoCompleteListenerCallback() {
            @Override
            public void onAutoComplete(ArrayList<String> poiltem) {
                for (int i = 0; i < poiltem.size(); i++) {
                    //리스트값을 addresslist로 add
                    addressList.add(poiltem.get(i));
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        ArrayAdapter<String> Adapter;
                        Adapter = new ArrayAdapter<String>(AutoComplete.this, android.R.layout.simple_list_item_1, addressList);
                        ListView list = (ListView) findViewById(R.id.list);
                        Adapter.notifyDataSetChanged();
                        list.setAdapter(Adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(AutoComplete.this, addressList.get(position), Toast.LENGTH_SHORT).show();
                                editText.setText(addressList.get(position));
                                //listview에서 클릭한 값으로 edittext값 변경
                                address_send = editText.getText().toString();
                            }
                        });
                    }
                });
            }
        });
    }

    public void onPass(View v) {
        //address_send 검색해서 받아온 주소이름
        Log.i("onPass", address_send);
        int position;
        Intent intent1 = getIntent();
        //위 경도값도 같이 보내기
        //받는 곳 inputActivity에서 배열로 저장 후 마커 찍기
        AddressInfo info = new AddressInfo();
        position = intent1.getIntExtra("position",0);
        Intent intent = new Intent(AutoComplete.this,InputActivity.class);
        runOnUiThread(new Runnable() {
                          public void run() {
                              TMapData tdata = new TMapData();
                              tdata.findAllPOI(address_send, new TMapData.FindAllPOIListenerCallback() {
                                  @Override
                                  public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                                      String[] array;
                                      TMapPOIItem item2 = poiItem.get(poiItem.size() - 1);
                                      array = item2.getPOIPoint().toString().split(" ");
                                      Log.i("lat333", array[1]);
                                      lat = Double.parseDouble(array[1]);
                                      lon = Double.parseDouble(array[3]);
                                  }
                              });
                          }
                      });

        intent.putExtra("address_name",address_send);
        intent.putExtra("position",position);
        setResult(RESULT_OK,intent);
        finish();
    }
}
