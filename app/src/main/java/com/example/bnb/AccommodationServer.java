package com.example.bnb;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Locale;

public class AccommodationServer {
    private static List<Accommodation> accommodationsList = new ArrayList<>();
    private static List<String> workerAddresses = List.of(
            "192.168.0.6:5001",
            "192.168.0.6:5002",
            "192.168.0.6:5003"
    );

    public static void main(String[] args) {
        try {
            loadAccommodationsFromJson();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        int port = 4321;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new AccommodationHandler(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void loadAccommodationsFromJson() throws JSONException {
        try (BufferedReader reader = new BufferedReader(new FileReader("app/src/main/app_data/accommodation.json"))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONArray accommodationsArray = new JSONArray(new JSONTokener(jsonBuilder.toString()));
            for (int i = 0; i < accommodationsArray.length(); i++) {
                JSONObject accommodationObject = accommodationsArray.getJSONObject(i);
                String name = accommodationObject.getString("name");
                String location = accommodationObject.getString("location");
                int capacity = accommodationObject.getInt("capacity");
                double pricePerNight = accommodationObject.getDouble("pricePerNight");
                float rating = (float) accommodationObject.getDouble("rating");
                String imagePath = accommodationObject.getString("imagePath");
                int numberOfReviews = accommodationObject.optInt("numberOfReviews", 0);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                JSONArray startDatesArray = accommodationObject.getJSONArray("availableStartDates");
                ArrayList<Date> availableStartDates = new ArrayList<>();
                for (int j = 0; j < startDatesArray.length(); j++) {
                    availableStartDates.add(sdf.parse(startDatesArray.getString(j)));
                }

                JSONArray endDatesArray = accommodationObject.getJSONArray("availableEndDates");
                ArrayList<Date> availableEndDates = new ArrayList<>();
                for (int j = 0; j < endDatesArray.length(); j++) {
                    availableEndDates.add(sdf.parse(endDatesArray.getString(j)));
                }

                JSONArray jsonBookings = accommodationObject.getJSONArray("bookings");
                ArrayList<Booking> bookings = new ArrayList<>();
                for (int j = 0; j < jsonBookings.length(); j++) {
                    JSONObject bookingObject = jsonBookings.getJSONObject(j);
                    String bookingId = bookingObject.getString("bookingId");
                    String userId = bookingObject.getString("userId");
                    Date startDate = sdf.parse(bookingObject.getString("startDate"));
                    Date endDate = sdf.parse(bookingObject.getString("endDate"));
                    Booking booking = new Booking(bookingId, userId, startDate, endDate);
                    bookings.add(booking);
                }

                String managerId = accommodationObject.getString("managerId");

                Accommodation accommodation = new Accommodation(
                        name, location, capacity, availableStartDates, availableEndDates,
                        pricePerNight, rating, imagePath, bookings, managerId,numberOfReviews
                );
                accommodationsList.add(accommodation);
            }
        } catch (FileNotFoundException e) {
            System.err.println("The JSON file was not found, starting with an empty list.");
        } catch (IOException | ParseException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        }
    }

    private static class AccommodationHandler extends Thread {
        private Socket socket;

        public AccommodationHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                input = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Object obj = input.readObject();
                    if (obj instanceof Accommodation) {
                        Accommodation accommodation = (Accommodation) obj;
                        synchronized (accommodationsList) {
                            accommodationsList.add(accommodation);
                            sendToWorker(accommodation);
                            saveAccommodationsToJson();
                            accommodationsList.notifyAll(); //Notify other threads waiting on this resource
                        }
                        System.out.println("Received and added accommodation: " + accommodation.getName());
                        output.writeObject("Accommodation " + accommodation.getName() + " added successfully");
                        output.flush();
                    } else if (obj instanceof String) {
                        String command = (String) obj;
                        if ("view".equals(command)) {
                            String managerId = (String) input.readObject();
                            synchronized (accommodationsList) {
                                while (accommodationsList.isEmpty()) {
                                    accommodationsList.wait(); //Wait for data to be available
                                }
                                String response = viewAccommodations(managerId);
                                output.writeObject(response);
                                output.flush();
                            }
                        } else if ("search".equals(command)) {
                            String filtersString = (String) input.readObject();
                            JSONObject filters = new JSONObject(filtersString);
                            synchronized (accommodationsList) {
                                while (accommodationsList.isEmpty()) {
                                    accommodationsList.wait(); //Wait for data to be available
                                }
                                String response = searchAccommodations(filters);
                                output.writeObject(response);
                                output.flush();
                            }
                        } else if ("update".equals(command)) {
                            Accommodation accommodation = (Accommodation) input.readObject();
                            synchronized (accommodationsList) {
                                updateAccommodation(accommodation);
                                accommodationsList.notifyAll(); //Notify other threads waiting on this resource
                            }
                            output.writeObject("Accommodation updated successfully");
                            output.flush();
                        } else if ("viewBookings".equals(command)) {
                            String userId = (String) input.readObject();
                            synchronized (accommodationsList) {
                                while (accommodationsList.isEmpty()) {
                                    accommodationsList.wait(); //Wait for data to be available
                                }
                                String response = viewBookings(userId);
                                output.writeObject(response);
                                output.flush();
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                System.err.println("Thread was interrupted: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket or streams: " + e.getMessage());
                }
            }
        }



        private void updateAccommodation(Accommodation accommodation) {
            synchronized (accommodationsList) {
                for (int i = 0; i < accommodationsList.size(); i++) {
                    if (accommodationsList.get(i).getName().equals(accommodation.getName())) {
                        accommodationsList.set(i, accommodation);
                        break;
                    }
                }
                try {
                    saveAccommodationsToJson();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private String viewBookings(String userId) throws JSONException {
            JSONArray bookingsArray = new JSONArray();
            synchronized (accommodationsList) {
                for (Accommodation accommodation : accommodationsList) {
                    for (Booking booking : accommodation.getBookings()) {
                        if (booking.getUserId().equals(userId)) {
                            JSONObject bookingJson = new JSONObject();
                            bookingJson.put("accommodation", accommodation.toJSON());
                            bookingJson.put("booking", booking.toJSON());
                            bookingsArray.put(bookingJson);
                        }
                    }
                }
            }
            return bookingsArray.toString();
        }




        private void sendToWorker(Accommodation accommodation) {
            int workerIndex = Math.abs(accommodation.getName().hashCode()) % workerAddresses.size();
            String workerAddress = workerAddresses.get(workerIndex);
            String[] parts = workerAddress.split(":"); //split the address into IP and port

            Socket workerSocket = null;
            ObjectOutputStream toWorker = null;
            ObjectInputStream fromWorker = null;

            try {
                //establish connection to the worker server
                workerSocket = new Socket(parts[0], Integer.parseInt(parts[1]));
                Socket backupWorketSocket = new Socket("192.168.0.6", 5004);
                toWorker = new ObjectOutputStream(workerSocket.getOutputStream());
                ObjectOutputStream backupWorker = new ObjectOutputStream(backupWorketSocket.getOutputStream());
                backupWorker.writeObject(accommodation);
                toWorker.writeObject(accommodation); //send the accommodation to the worker
                toWorker.flush();

                fromWorker = new ObjectInputStream(workerSocket.getInputStream());
                String ack = (String) fromWorker.readObject();
                System.out.println("Acknowledgment from worker: " + ack);

            } catch (IOException e) {
                System.err.println("IO Error when communicating with worker: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found error when reading acknowledgment: " + e.getMessage());
            } finally {
                closeResource(fromWorker);
                closeResource(toWorker);
                closeResource(workerSocket);
            }
        }

        private void closeResource(Closeable resource) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (IOException e) {
                    System.err.println("Failed to close resource: " + e.getMessage());
                }
            }
        }

        private void saveAccommodationsToJson() throws JSONException {
            JSONArray accommodationsArray = new JSONArray();
            for (Accommodation accommodation : accommodationsList) {
                accommodationsArray.put(accommodation.toJSON());
            }

            try (FileWriter file = new FileWriter("app/src/main/app_data/accommodation.json")) {
                file.write(accommodationsArray.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String viewAccommodations(String managerId) throws JSONException {
            JSONArray accommodationsArray = new JSONArray();
            for (Accommodation accommodation : accommodationsList) {
                if (accommodation.getManagerId().equals(managerId)) {
                    accommodationsArray.put(accommodation.toJSON());
                }
            }
            return accommodationsArray.toString(4);
        }

        private String searchAccommodations(JSONObject filters) throws JSONException {
            List<Accommodation> filteredAccommodations = new ArrayList<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = null;
            Date endDate = null;

            String location = filters.optString("location", null);
            String startDateStr = filters.optString("startDate", null);
            String endDateStr = filters.optString("endDate", null);
            int capacity = filters.optInt("capacity", -1);
            double minPrice = filters.optDouble("minPrice", -1);
            double maxPrice = filters.optDouble("maxPrice", -1);
            float rating = (float) filters.optDouble("rating", -1);

            try {
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    startDate = dateFormatter.parse(startDateStr);
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    endDate = dateFormatter.parse(endDateStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            synchronized (accommodationsList) {
                for (Accommodation accommodation : accommodationsList) {
                    boolean matches = true;

                    if (location != null && !location.isEmpty() && !accommodation.getLocation().equalsIgnoreCase(location)) {
                        matches = false;
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
                    if (startDate != null && endDate != null) {
                        boolean dateMatch = false;
                        for (int i = 0; i < accommodation.getAvailableStartDates().size(); i++) {
                            Date availableStartDate = accommodation.getAvailableStartDates().get(i);
                            Date availableEndDate = accommodation.getAvailableEndDates().get(i);
                            if ((startDate.compareTo(availableStartDate) >= 0 && startDate.compareTo(availableEndDate) <= 0) &&
                                    (endDate.compareTo(availableStartDate) >= 0 && endDate.compareTo(availableEndDate) <= 0)) {
                                dateMatch = true;
                                break;
                            }
                        }
                        if (!dateMatch) {
                            matches = false;
                        }
                    }

                    if (matches) {
                        filteredAccommodations.add(accommodation);
                    }
                }
            }

            JSONArray resultArray = new JSONArray();
            for (Accommodation accommodation : filteredAccommodations) {
                resultArray.put(accommodation.toJSON());
            }
            return resultArray.toString();
        }

    }
}

