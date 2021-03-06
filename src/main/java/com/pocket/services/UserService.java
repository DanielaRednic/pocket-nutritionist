package com.pocket.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import com.pocket.model.User;
import com.pocket.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.lang.*;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class UserService {

    private static List<User> users ;
    private static final Path USERS_PATH = FileSystemService.getPathToFile( "users.json");

    public static void loadUsersFromFile() throws IOException {

        if (!Files.exists(USERS_PATH)) {
            FileUtils.copyURLToFile(UserService.class.getClassLoader().getResource("users.json"), USERS_PATH.toFile());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(USERS_PATH.toFile(), new TypeReference<List<User>>() {
        });
    }

    public static List<User> loadUsersFromFile2() throws IOException {

        if (!Files.exists(USERS_PATH)) {
            FileUtils.copyURLToFile(UserService.class.getClassLoader().getResource("users.json"), USERS_PATH.toFile());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(USERS_PATH.toFile(), new TypeReference<List<User>>() {
        });
        return users;
    }

    public static void addClientUser(String username, String password, String role, LocalDate DateOfBirth, String PhoneNumber, String Email, String FullName, String Gender, String Allergies, String Height, String Weight, String DietType,Boolean verified) throws EmptyEntryException, UsernameAlreadyExistsException, InvalidPhoneNumberException, InvalidEmailException {
        checkEmptyEntry(username, DateOfBirth, PhoneNumber, Email , FullName);
        checkUserDoesNotAlreadyExist(username);
        checkPhoneNumberIsValid(PhoneNumber);
        try {
            checkHeightIsValid(Height);
        } catch (InvalidHeightException e) {
            e.printStackTrace();
        }
        try {
            checkWeightIsValid(Weight);
        } catch (InvalidWeightException e) {
            e.printStackTrace();
        }
        checkEmailIsValid(Email);
        users.add(new User(username, encodePassword(username, password), role, DateOfBirth, PhoneNumber, Email, FullName, Gender, Allergies, Height, Weight, DietType, verified));
        persistUsers();
    }

    public static void addNutritionistUser(String username, String password, String role, LocalDate DateOfBirth, String PhoneNumber, String Email, String FullName, Boolean verified, String path) throws EmptyEntryException, UsernameAlreadyExistsException, InvalidPhoneNumberException, InvalidEmailException, IOException, NoFileSelectedException {
        checkEmptyEntry(username, DateOfBirth, PhoneNumber, Email, FullName);
        checkUserDoesNotAlreadyExist(username);
        checkPhoneNumberIsValid(PhoneNumber);
        checkEmailIsValid(Email);
        checkFilePath(path);
        users.add(new User(username, encodePassword(username, password), role, DateOfBirth, PhoneNumber, Email, FullName, verified));
        copyImage(path,username);
        persistUsers();
    }

    private static void checkFilePath(String path) throws NoFileSelectedException
    {
        if(path=="" || path==null)
            throw new NoFileSelectedException();
    }

    private static void copyImage(String path, String username) throws IOException
    {
        File source = new File(path);
        Path desti = Paths.get("Documents");

        File dest = new File(desti.toString() + "\\" + username + "." + getExtension(source.toString()));
        copyFile(source, dest);
    }



    private static void checkEmptyEntry(String username, LocalDate DateOfBirth, String PhoneNumber, String Email, String FullName) throws EmptyEntryException {
        if (username.compareTo("")==0 || DateOfBirth == null || PhoneNumber.compareTo("")==0 || Email.compareTo("")==0 || FullName.compareTo("")==0)
        {
            throw new EmptyEntryException();
        }
        boolean usernameValid = false;
        for(int i=0;i<username.length();i++)
        {
            char c = username.charAt(i);
            if(c!=' ')
            {
                usernameValid = true;
            }
        }
        if(usernameValid == false)
            throw new EmptyEntryException();
    }

    private static void checkUserDoesNotAlreadyExist(String username) throws UsernameAlreadyExistsException {
        try{
            loadUsersFromFile();
        }catch (IOException e){
            e.printStackTrace();
            e.getCause();
        }
        for (User user : users) {
            if (Objects.equals(username, user.getUsername()))
                throw new UsernameAlreadyExistsException(username);
        }
    }

    private static void checkPhoneNumberIsValid(String phone) throws InvalidPhoneNumberException {
        //boolean isValid = true;

        for(int i=0;i<phone.length();i++)
        {
            char c = phone.charAt(i);
            if(Character.isDigit(c)==false)
            {
                throw new InvalidPhoneNumberException();
            }
        }


    }
    private static void checkHeightIsValid(String height) throws InvalidHeightException {

        for(int i=0;i<height.length();i++)
        {
            char c = height.charAt(i);
            if(Character.isDigit(c)==false)
            {
                throw new InvalidHeightException();
            }
        }
    }
    private static void checkWeightIsValid(String weight) throws InvalidWeightException {

        for(int i=0;i<weight.length();i++)
        {
            char c = weight.charAt(i);
            if(Character.isDigit(c)==false)
            {
                throw new InvalidWeightException();
            }
        }
    }

    private static void checkEmailIsValid(String email) throws InvalidEmailException {
        boolean isValid = false;
        for(int i=0;i<email.length();i++)
        {
            char c = email.charAt(i);
            if(c=='@')
            {
                isValid = true;
            }
        }
        if(isValid==false)
        {
            throw new InvalidEmailException();
        }
    }

    private static void persistUsers() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(USERS_PATH.toFile(), users);
        } catch (IOException e) {
            throw new CouldNotWriteUsersException();
        }
    }
    public static void persistUsers2(List<User> changes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(USERS_PATH.toFile(), changes);
        } catch (IOException e) {
            throw new CouldNotWriteUsersException();
        }
    }

    public static String loginEncode(String salt, String password)
    {
        return encodePassword(salt,password);
    }

    private static String encodePassword(String salt, String password) {
        MessageDigest md = getMessageDigest();
        md.update(salt.getBytes(StandardCharsets.UTF_8));

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // This is the way a password should be encoded when checking the credentials
        return new String(hashedPassword, StandardCharsets.UTF_8)
                .replace("\"", ""); //to be able to save in JSON format
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 does not exist!");
        }
        return md;
    }


}
