package com.obadiahpcrowe.stirling.localisation.translation;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.localisation.translation.obj.TranslateResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 1:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation
 * Copyright (c) Obadiah Crowe 2017
 */
public class TranslateManager {

    private static TranslateManager instance;
    private final String URL = "https://translate.google.com/translate_a/single?client=at&dt=t&dt=ld&dt=qca&dt=rm&dt=bd" +
      "&dj=1&hl=es-ES&ie=UTF-8&oe=UTF-8&inputm=2&otf=2&iid=1dd3b944-fa62-4b55-b330-74909a99969e";
    private Gson gson;

    private TranslateManager() {
        this.gson = new Gson();
    }

    public String translate(String text, StirlingLocale input, StirlingLocale output) {
        final String br = "!!!BREAK_HERE!!!";

        StringBuilder parts = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i > 0 && (i % 400 == 0)) {
                parts.append(br);
            }
            parts.append(text.charAt(i));
        }

        Map<Integer, TranslateResponse> responses = Maps.newHashMap();
        List<Thread> threads = Lists.newArrayList();

        String[] p = parts.toString().split(br);
        int counter = 0;
        for (String part : p) {
            final int c = counter;
            Thread t = new Thread(() -> {
                responses.put(c, translatePart(part, input, output));
            });
            t.start();
            threads.add(t);
            counter++;
        }

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        StringBuilder builder = new StringBuilder();
        responses.forEach((i, r) -> r.getSentences().forEach(s -> builder.append(s.getTrans())));

        return builder.toString();
    }

    private TranslateResponse translatePart(String text, StirlingLocale input, StirlingLocale output) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(URL);

        List<NameValuePair> pairs = Lists.newArrayList();
        pairs.add(new BasicNameValuePair("sl", input.getLocaleCode()));
        pairs.add(new BasicNameValuePair("tl", output.getLocaleCode()));
        pairs.add(new BasicNameValuePair("q", text));


        request.addHeader("Encoding", "UTF-8");
        request.addHeader("User-Agent", "AndroidTranslate/5.3.0.RC02.130475354-53000263 5.1 phone TRANSLATE_OPM5_TEST_1");
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return gson.fromJson(result.toString(), TranslateResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TranslateManager getInstance() {
        if (instance == null)
            instance = new TranslateManager();
        return instance;
    }
}
