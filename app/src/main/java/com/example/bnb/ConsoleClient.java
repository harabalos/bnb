package com.example.bnb;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsoleClient {
    private String host;
    private int port;
    private List<User> users = new ArrayList<>();
    private List<Accommodation> accommodations = new ArrayList<>();
    private static final String USER_FILE_PATH = "app_data/users.json";
    private static final String ACCOMMODATION_FILE_PATH = "app_data/accommodation.json";
    private WeakReference<Context> contextRef;

    public ConsoleClient(String host, int port, Context context) {
        this.host = host;
        this.port = port;
        this.contextRef = new WeakReference<>(context);
        try {
            loadUsersFromFile();
            loadAccommodationsFromFile();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUsersFromFile() throws JSONException {
        Context context = contextRef.get();
        if (context == null) {
            return;
        }

        File userFile = new File(context.getFilesDir(), USER_FILE_PATH);

        Log.d("ConsoleClient", "Loading users from: " + userFile.getAbsolutePath());

        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(userFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONArray userArray = new JSONArray(new JSONTokener(jsonBuilder.toString()));
            for (int i = 0; i < userArray.length(); i++) {
                JSONObject userObject = userArray.getJSONObject(i);
                String id = userObject.getString("id");
                String username = userObject.getString("username");
                String password = userObject.getString("password");
                boolean isManager = userObject.getBoolean("isManager");
                User user = new User(username, password, isManager);
                user.setId(id);
                users.add(user);
            }
        } catch (IOException e) {
            Log.e("ConsoleClient", "Error reading users.json file: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("ConsoleClient", "Error parsing users.json file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadAccommodationsFromFile() {
        Context context = contextRef.get();
        if (context == null) {
            return;
        }

        File accommodationFile = new File(context.getFilesDir(), ACCOMMODATION_FILE_PATH);
        if (!accommodationFile.exists()) {
            Log.e("ConsoleClient", "Accommodation file does not exist: " + accommodationFile.getAbsolutePath());
            return;
        }

        Log.d("ConsoleClient", "Loading accommodations from: " + accommodationFile.getAbsolutePath());

        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(accommodationFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONArray accommodationArray = new JSONArray(new JSONTokener(jsonBuilder.toString()));
            accommodations.clear(); //clear the existing list before loading new data
            for (int i = 0; i < accommodationArray.length(); i++) {
                accommodations.add(Accommodation.fromJson(accommodationArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            Log.e("ConsoleClient", "Error reading accommodation.json file: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("ConsoleClient", "Error parsing accommodation.json file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void saveUsersToFile() throws JSONException {
        JSONArray userArray = new JSONArray();
        for (User user : users) {
            JSONObject userObject = new JSONObject();
            userObject.put("id", user.getId());
            userObject.put("username", user.getUsername());
            userObject.put("password", user.getPassword());
            userObject.put("isManager", user.isManager());
            userArray.put(userObject);
        }

        Context context = contextRef.get();
        if (context == null) {
            return;
        }

        File userFile = new File(context.getFilesDir(), USER_FILE_PATH);
        if (!userFile.getParentFile().exists()) {
            userFile.getParentFile().mkdirs();
        }

        try (FileWriter file = new FileWriter(userFile)) {
            Log.d("ConsoleClient", "Saving users to: " + userFile.getAbsolutePath());
            file.write(userArray.toString());
        } catch (IOException e) {
            Log.e("ConsoleClient", "Error saving users to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveAccommodationsToFile() throws JSONException {
        JSONArray accommodationArray = new JSONArray();
        for (Accommodation accommodation : accommodations) {
            accommodationArray.put(accommodation.toJSON());
        }

        Context context = contextRef.get();
        if (context == null) {
            return;
        }

        File accommodationFile = new File(context.getFilesDir(), ACCOMMODATION_FILE_PATH);
        if (!accommodationFile.getParentFile().exists()) {
            accommodationFile.getParentFile().mkdirs();
        }

        try (FileWriter file = new FileWriter(accommodationFile)) {
            Log.d("ConsoleClient", "Saving accommodations to: " + accommodationFile.getAbsolutePath());
            file.write(accommodationArray.toString());
        } catch (IOException e) {
            Log.e("ConsoleClient", "Error saving accommodations to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean addUser(User user) {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                return false;
            }
        }
        users.add(user);
        try {
            saveUsersToFile();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean addAccommodation(Accommodation accommodation) {
        accommodations.add(accommodation);
        try {
            saveAccommodationsToFile();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void authenticateAsync(String username, String password, AuthenticationCallback callback) {
        new AuthenticateTask(this, callback).execute(username, password);
    }

    public void addAccommodationAsync(Accommodation accommodation, AccommodationCallback callback) {
        new AddAccommodationTask(this, callback).execute(accommodation);
    }

    public void viewAccommodationsAsync(String managerId, ViewAccommodationsCallback callback) {
        new ViewAccommodationsTask(this, callback).execute(managerId);
    }

    public interface AuthenticationCallback {
        void onAuthenticationResult(User user);
    }


    public interface ViewAccommodationsCallback {
        void onViewAccommodationsResult(String response);
    }

    private static class AuthenticateTask extends AsyncTask<String, Void, User> {
        private WeakReference<ConsoleClient> clientRef;
        private AuthenticationCallback callback;

        AuthenticateTask(ConsoleClient client, AuthenticationCallback callback) {
            this.clientRef = new WeakReference<>(client);
            this.callback = callback;
        }

        @Override
        protected User doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            ConsoleClient client = clientRef.get();
            return client != null ? client.authenticate(username, password) : null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (callback != null) {
                callback.onAuthenticationResult(user);
            }
        }
    }

    private static class AddAccommodationTask extends AsyncTask<Accommodation, Void, String> {
        private WeakReference<ConsoleClient> clientRef;
        private AccommodationCallback callback;

        AddAccommodationTask(ConsoleClient client, AccommodationCallback callback) {
            this.clientRef = new WeakReference<>(client);
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Accommodation... params) {
            Accommodation accommodation = params[0];
            ConsoleClient client = clientRef.get();
            if (client == null) {
                return null;
            }

            try (Socket socket = new Socket(client.host, client.port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject(accommodation);
                return (String) in.readObject();
            } catch (Exception e) {
                Log.e("ConsoleClient", "Error adding accommodation", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (callback != null) {
                callback.onAccommodationResult(response);
            }
        }
    }

    private static class ViewAccommodationsTask extends AsyncTask<String, Void, String> {
        private WeakReference<ConsoleClient> clientRef;
        private ViewAccommodationsCallback callback;

        ViewAccommodationsTask(ConsoleClient client, ViewAccommodationsCallback callback) {
            this.clientRef = new WeakReference<>(client);
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String managerId = params[0];
            ConsoleClient client = clientRef.get();
            if (client == null) {
                return null;
            }

            try (Socket socket = new Socket(client.host, client.port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("view");
                out.writeObject(managerId);
                return (String) in.readObject();
            } catch (Exception e) {
                Log.e("ConsoleClient", "Error viewing accommodations", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (callback != null) {
                callback.onViewAccommodationsResult(response);
            }
        }
    }
    public void searchAccommodationsAsync(String location, String startDate, String endDate, int capacity, double minPrice, double maxPrice, float rating, SearchCallback callback) {
        new SearchAccommodationsTask(this, callback).execute(location, startDate, endDate, capacity, minPrice, maxPrice, rating);
    }

    private static class SearchAccommodationsTask extends AsyncTask<Object, Void, List<Accommodation>> {
        private WeakReference<ConsoleClient> clientRef;
        private SearchCallback callback;

        SearchAccommodationsTask(ConsoleClient client, SearchCallback callback) {
            this.clientRef = new WeakReference<>(client);
            this.callback = callback;
        }

        @Override
        protected List<Accommodation> doInBackground(Object... params) {
            String location = (String) params[0];
            String startDate = (String) params[1];
            String endDate = (String) params[2];
            int capacity = (int) params[3];
            double minPrice = (double) params[4];
            double maxPrice = (double) params[5];
            float rating = (float) params[6];

            ConsoleClient client = clientRef.get();
            if (client == null) {
                return new ArrayList<>();
            }

            List<Accommodation> filteredAccommodations = new ArrayList<>();
            for (Accommodation accommodation : client.accommodations) {
                boolean matches = true;

                if (!location.isEmpty() && !accommodation.getLocation().equalsIgnoreCase(location)) {
                    matches = false;
                }

                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date start = dateFormatter.parse(startDate);
                        Date end = dateFormatter.parse(endDate);
                        boolean dateMatch = false;
                        for (int i = 0; i < accommodation.getAvailableStartDates().size(); i++) {
                            Date availableStart = accommodation.getAvailableStartDates().get(i);
                            Date availableEnd = accommodation.getAvailableEndDates().get(i);
                            if (!start.before(availableStart) && !end.after(availableEnd)) {
                                dateMatch = true;
                                break;
                            }
                        }
                        if (!dateMatch) {
                            matches = false;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        matches = false;
                    }
                }

                if (capacity != -1 && accommodation.getCapacity() < capacity) {
                    matches = false;
                }

                if (minPrice != -1 && accommodation.getPricePerNight() < minPrice) {
                    matches = false;
                }

                if (maxPrice != -1 && accommodation.getPricePerNight() > maxPrice) {
                    matches = false;
                }

                if (rating != -1 && accommodation.getRating() < rating) {
                    matches = false;
                }

                if (matches) {
                    filteredAccommodations.add(accommodation);
                }
            }

            return filteredAccommodations;
        }

        @Override
        protected void onPostExecute(List<Accommodation> accommodations) {
            if (callback != null) {
                callback.onSearchResult(accommodations);
            }
        }
    }

    public interface SearchCallback {
        void onSearchResult(List<Accommodation> accommodations);
    }

    public interface AccommodationCallback {
        void onAccommodationResult(String response);
    }

    public void updateAccommodationAsync(Accommodation accommodation, AccommodationCallback callback) {
        new UpdateAccommodationTask(this, callback).execute(accommodation);
    }

    private static class UpdateAccommodationTask extends AsyncTask<Accommodation, Void, String> {
        private WeakReference<ConsoleClient> clientRef;
        private AccommodationCallback callback;

        UpdateAccommodationTask(ConsoleClient client, AccommodationCallback callback) {
            this.clientRef = new WeakReference<>(client);
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Accommodation... params) {
            Accommodation accommodation = params[0];
            ConsoleClient client = clientRef.get();
            if (client == null) {
                return null;
            }

            try (Socket socket = new Socket(client.host, client.port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("update");
                out.writeObject(accommodation);
                return (String) in.readObject();
            } catch (Exception e) {
                Log.e("ConsoleClient", "Error updating accommodation", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (callback != null) {
                callback.onAccommodationResult(response);
            }
        }
    }



}
