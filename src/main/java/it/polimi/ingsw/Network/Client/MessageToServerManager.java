package it.polimi.ingsw.Network.Client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Enums.ActionTokenType;
import it.polimi.ingsw.Enums.PopeFavorCardState;
import it.polimi.ingsw.Enums.Resource;
import it.polimi.ingsw.Messages.InGameMessages.InGameMessage;
import it.polimi.ingsw.Messages.PreGameMessages.PreGameMessage;
import it.polimi.ingsw.Network.AssetDescriptor;
import it.polimi.ingsw.Network.Client.Messages.*;
import it.polimi.ingsw.Network.Receiver;
import it.polimi.ingsw.Network.Sender;
import it.polimi.ingsw.View.View;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Manages the messages from/to the player in an online game
 */
public class MessageToServerManager implements Runnable, MessageManager{

    private Receiver fromServer;
    private Sender toServer;
    private final Visitor visitor;
    private final Gson gson;
    private final JsonParser parser;

    /**
     * Constructor
     * @param view view modality chosen by the player
     */
    public MessageToServerManager(View view){
        this.visitor = new Visitor(view);
        this.gson = new Gson();
        this. parser = new JsonParser();
    }

    /**
     * Applies the changes deriving from the message received
     * @param input message received through the internet
     * @return true if a message of game over has been received, false otherwise
     */
    private boolean convertInput(String input){
        FromServerMessage message = interpret(input);
        if (message == null){
            return false;
        }
        return message.visit(visitor);
    }

    /**
     * Converts the string received from internet in a message
     * @param input string received from internet
     * @return the interpreted message
     */
    private FromServerMessage interpret(String input){
        FromServerMessage result = null;
        if (input == null){
            return null;
        }
        JsonElement element = parser.parse(input);
        if (!element.isJsonObject()){
            throw new IllegalArgumentException("Check the config file and his syntax");
        }
        else{
            JsonObject toEvaluate = element.getAsJsonObject();
                try{
                    switch(toEvaluate.get("type").getAsString()){
                        case "changedLobby":
                            result = new ChangedLobbyMessage(toEvaluate.get("id").getAsInt(), gson.fromJson(toEvaluate.get("players"), String[].class), toEvaluate.get("itsYou").getAsBoolean());
                            break;
                        case "init":
                            JsonArray playersId= toEvaluate.get("playersId").getAsJsonArray();
                            JsonArray playersUsernames = toEvaluate.get("playersUsernames").getAsJsonArray();
                            int[] ids = new int[playersId.size()];
                            String[] usernames = new String[playersUsernames.size()];

                            for(int j = 0; j < playersId.size(); j++){
                                ids[j] = playersId.get(j).getAsInt();
                                usernames[j] = playersUsernames.get(j).getAsString();
                            }

                            int[] clientLeaderCards = gson.fromJson(toEvaluate.get("localPlayerLeaderCards"), int[].class);

                            result = new InitMessage(toEvaluate.get("id").getAsInt(),gson.fromJson(toEvaluate.get("market"), Resource[][].class), gson.fromJson(toEvaluate.get("slide"), Resource.class),gson.fromJson(toEvaluate.get("devDecks"), int[].class), ids, usernames, clientLeaderCards);
                            break;
                        case "start":
                            result = (new StartMessage());
                            break;
                        case "actionOnLeaderCard":
                            result = (new ActionOnLeaderCardMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("discard").getAsBoolean(), toEvaluate.get("cardId").getAsInt()));
                            break;
                        case "newDevCard":
                            result = (new NewDevCardMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("cardId").getAsInt(), toEvaluate.get("numberOfSlot").getAsInt()));
                            break;
                        case "newTopCard":
                            result = (new NewTopCardMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("numberOfDeck").getAsInt()));
                            break;
                        case "changedShelf":
                            result = (new ChangedShelfMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("numberOfShelf").getAsInt(), gson.fromJson(toEvaluate.get("resourceType"), Resource.class),toEvaluate.get("quantity").getAsInt()));
                            break;
                        case "changedStrongbox":
                            result = (new ChangedStrongboxMessage(toEvaluate.get("id").getAsInt(), gson.fromJson(toEvaluate.get("inside"), new TypeToken<HashMap<Resource, Integer>>(){}.getType())));
                            break;
                        case "changedSupportContainer":
                            result = (new ChangedSupportContainerMessage(toEvaluate.get("id").getAsInt(), gson.fromJson(toEvaluate.get("inside"), new TypeToken<HashMap<Resource, Integer>>(){}.getType())));
                            break;
                        case "changedLeaderStorage":
                            result = (new ChangedLeaderStorageMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("cardId").getAsInt(), gson.fromJson(toEvaluate.get("owned"), Resource[].class))) ;
                            break;
                        case "newMarketState":
                            result = (new NewMarketStateMessage(gson.fromJson(toEvaluate.get("grid"), Resource[][].class), gson.fromJson(toEvaluate.get("slide"), Resource.class)));
                            break;
                        case "popeFavourCardState":
                            result = (new PopeFavourCardStateMessage(toEvaluate.get("id").getAsInt(), gson.fromJson(toEvaluate.get("cards"), PopeFavorCardState[].class)));
                            break;
                        case "newPlayerPosition":
                            result = (new NewPlayerPositionMessage(toEvaluate.get("id").getAsInt(), toEvaluate.get("position").getAsInt()));
                            break;
                        case "winner":
                            result = (new WinnerMessage(toEvaluate.get("id").getAsInt()));
                            break;
                        case "turnOf":
                            result = (new TurnOfMessage(toEvaluate.get("id").getAsInt()));
                            break;
                        case "error":
                            result = (new ErrorMessage(toEvaluate.get("error").getAsString()));
                            break;
                        case "selectionError":
                            result = new SelectionErrorMessage(toEvaluate.get("error").getAsString(), toEvaluate.get("id").getAsInt());
                            break;
                        case "disconnectionError":
                            result = new DisconnectionMessage(toEvaluate.get("error").getAsString(), toEvaluate.get("id").getAsInt());
                            break;
                        case "lorenzoTurn":
                            result = new LorenzoTurnMessage(gson.fromJson(toEvaluate.get("actionToken"), ActionTokenType.class));
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e){
                    //there is some format error inside the message
                }
            }
        return result;
    }

    /**
     * Loop for constant listening from the server
     */
    @Override
    public void run(){
        if(fromServer != null && toServer != null) {
            String input;
            while (true) {
                try {
                    input = fromServer.readMessage();
                    if(convertInput(input))
                        break;
                }catch (IOException e){
                    break;
                }
            }
            fromServer.close();
            toServer.close();
        }
    }

    /**
     * Sends a InGameMessage message
     * @param message message to send
     */
    @Override
    public void update(InGameMessage message) {
        this.update(message.toJson());
    }

    /**
     * Sends a PreGameMessage to server
     * @param message message to send
     */
    @Override
    public void update(PreGameMessage message) {
        this.update(message.toJson());
    }

    /**
     * Sends the String to the server
     * @param inputLine sends a string to the server
     */
    public void update(String inputLine) {
        int i=0;
        while (!this.toServer.send(inputLine)){
            i++;
            if (i>=0){
                System.out.println("ERROR SENDING THE MESSAGE");
            }
            if (i==10){
                this.fromServer.close();
                this.toServer.close();
                new DisconnectionMessage("Error on the internet", 404).visit(this.visitor);
                break;
            }
        }
    }

    /**
     * Connects this client to the server and updates all the new assets
     * @param ip ip to connect
     * @param port port to connect
     * @param username Username of the client
     */
    @Override
    public void connect(String ip, String port, String username) {
        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            socket.setKeepAlive(true);
            fromServer = new Receiver(socket.getInputStream());
            toServer = new Sender(socket.getOutputStream());
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Server unreachable");
            return;
        }
        this.updateAssets();
        this.update(username);
        new Thread(this).start();
    }

    //---Init-assets-part-----------------------------
    /**
     * This method create an send a message expecting some new files in return
     */
    private void updateAssets(){
        String[] hashingAlg = {"SHA-256"};
        MessageDigest messageDigest = null;
        for (String alg : hashingAlg){
            try{
                messageDigest = MessageDigest.getInstance(alg);
            }catch (NoSuchAlgorithmException ignored){
            }
        }

        String[] paths = {File.separator + "assets" + File.separator + "imgs" + File.separator, File.separator + "JSONs" + File.separator};
        AssetDescriptor assetDesc;
        for (String relativePath : paths){
            String fullPath ;
            try {
                fullPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + relativePath;
            } catch (URISyntaxException e) {
                return;
            }
            if (this.toServer.sendAssetMessage(this.getAssetsMessage(fullPath, (messageDigest==null) ? "size" : messageDigest.getAlgorithm()), relativePath, (messageDigest == null) ? "size" : messageDigest.getAlgorithm())){
                try{
                    while((assetDesc = this.fromServer.getAssetDescriptor())!=null){
                        this.saveAsset(assetDesc, fullPath);
                    }
                }catch (IOException e){
                    System.exit(1);
                }
            }
        }
        this.toServer.sendMessageEndAssets();
    }

    /**
     * Saves a file to a specific path
     * @param assetDescriptor this object holds all the infos of a file arrived
     * @param path the path to save the file into
     */
    private void saveAsset(AssetDescriptor assetDescriptor, String path){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path + assetDescriptor.getName(), false);
        }catch (FileNotFoundException e){
            System.exit(500);
        }
        int count;
        byte[] buffer = new byte[1024];
        try {
            while ((count = assetDescriptor.getAssetByteArrayI().read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
        }catch (IOException ignored){
        }
        try {
            fileOutputStream.close();
        }catch (IOException ignored){
            //
        }
    }

    /**
     * Calculate the hash of a file
     * @param shaAlg hash algorithm to use
     * @param file tile to evaluate
     * @return the file hash
     */
    private String getFileSha(String shaAlg, File file){
        MessageDigest digest = null;
        int totalBytes = 0;
        if (!shaAlg.equals("size")){
            try {
                digest = MessageDigest.getInstance(shaAlg);
            }
            catch (NoSuchAlgorithmException e){
                return "null";
            }
        }

        try {
            FileInputStream fileInputS = new FileInputStream(file);
            //chunk of data = 1Kb
            byte[] byteArr = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fileInputS.read(byteArr)) != -1) {
                if (shaAlg.equals("size")){
                    totalBytes += bytesRead;
                }
                else {
                    digest.update(byteArr, 0, bytesRead);
                }
            }
            fileInputS.close();
        }catch (IOException e){
            return "null";
        }

        if (shaAlg.equals("size")){
            return "" + totalBytes;
        }

        byte[] hashBytes = digest.digest();
        StringBuilder strBuilder = new StringBuilder();
        for (byte singleByte : hashBytes) {
            strBuilder.append(Integer.toString((singleByte & 0xff) + 0x100, 16).substring(1));
        }
        return strBuilder.toString();
    }

    /**
     * Builds a message that specifies all the files in a folder and those files hash listed one by one
     * @param fullPath path to evaluate
     * @param hashAlg hash algorithm to use to check files
     * @return the message
     */
    private String getAssetsMessage(String fullPath, String hashAlg){
        File targetDir = new File(fullPath);
        if (!targetDir.exists()){
             if(!targetDir.mkdirs()){
                 System.out.println("Debug: Grosso problema");
                 System.exit(1);
             }
        }

        File[] content = targetDir.listFiles();
        assert content != null;
        if (content.length == 0){
            return "[]";
        }
        StringBuilder message = new StringBuilder("[");
        for (int i = 0; i< Objects.requireNonNull(content).length-1; i++){
            if (content[i].isFile()) {
                message.append("{\"name\": \"").append(content[i].getName()).append("\", ").append("\"hash\": \"").append(this.getFileSha(hashAlg, content[i])).append("\"").append("},");
            }
        }
        File lastFile = content[content.length-1];
        if (lastFile.isFile()){
            message.append("{\"name\": \"").append(lastFile.getName()).append("\", ").append("\"hash\": \"").append(this.getFileSha(hashAlg, lastFile)).append("\"").append("}");
            message.append("]");
            return message.toString();
        }
        else{
            return message.substring(0,message.toString().length()-1) + "]";
        }
    }
    //End-init-assets-part-------------------------
}
