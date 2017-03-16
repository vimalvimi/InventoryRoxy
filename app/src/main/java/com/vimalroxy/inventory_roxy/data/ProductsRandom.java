package com.vimalroxy.inventory_roxy.data;

import java.util.Random;


public class ProductsRandom {

    private String randomName;
    private String randomImageURI;
    private int randomPrice;
    private int randomQuantity;
    private int randomSupplierNumber;
    private String randomSupplierName;

    //Random number so IMAGE and NAME can be same.
    private int randomNumberNameImage;

    public ProductsRandom() {

        //NAME
        randomNameGenerator();
        //IMAGE
        randomImageGenerator(randomNumberNameImage);
        //PRICE
        priceRandomGenerator(250, 750);
        //QUANTITY
        quantityRandomGenerator(10, 50);
        //SUPPLIER NAME
        supplierNameRandomGenerator();
        //SUPPLIER NUMBER
        supplierNumberRandomGenerator(50000, 60000);

    }

    //Generate random price in int within given range
    private void priceRandomGenerator(int minimum, int maximum) {
        Random randomGen = new Random();
        randomPrice = randomGen.nextInt((maximum - minimum) + 1) + minimum;
    }

    //Generate random quantity in int within given range
    private void quantityRandomGenerator(int minimum, int maximum) {
        Random randomGen = new Random();
        randomQuantity = randomGen.nextInt((maximum - minimum) + 1) + minimum;
    }

    //Generate random contact number in int within given range
    private void supplierNumberRandomGenerator(int minimum, int maximum) {
        Random randomGen = new Random();
        randomSupplierNumber = randomGen.nextInt((maximum - minimum) + 1) + minimum;
    }

    //Generate random supplier name in String from given array
    private void supplierNameRandomGenerator() {

        String[] supplierNames = {"Jeffie Jawad",
                "Madeleine Mcdonalds",
                "Chadwick Callen",
                "Lakia Luong",
                "Harris Hannigan",
                "Mariah Mcelroy",
                "Rupert Rehman",
                "Edmund Eisele",
                "Gala Garth",
                "Ginette Gundersen",
                "Irina Iler",
                "Quinn Quist",
                "Emanuel Esterline",
                "Calandra Courser",
                "Ivonne Imai",
                "Chaya Chagoya",
                "Hank Hoelscher",
                "Marica Morvant",
                "Cher Chevalier",
                "Marguerite Marker",
        };

        Random randomGen = new Random();
        int randomNum = randomGen.nextInt(supplierNames.length);
        randomSupplierName = (supplierNames[randomNum]);
    }

    //Generate random item name in String from given array
    private void randomNameGenerator() {

        String[] arrayName = {
                "LS2 Helmet Men",
                "Sennheiser 185",
                "TheBrocode Belt",
                "CalvinKlein Wallet",
                "Ferrari Sunglasses",
                "TheNorthFace Gloves",
                "TheNorthFace Pouch",
                "TonyMen Bracelet",
        };

        int min = 0;
        int max = 7;

        Random randomGen = new Random();
        randomNumberNameImage = randomGen.nextInt((max - min) + 1) + min;

        randomName = arrayName[randomNumberNameImage];
    }

    //Get relevant image to the product name
    private void randomImageGenerator(int givenNumber) {

        String uri = "android.resource://com.vimalroxy.inventory_roxy/drawable/";

        String[] arrayImage = {
                "ls",
                "sennheiser",
                "brocode_belt",
                "ck_doc_holder",
                "ferrari_sunglasses",
                "ttf_gloves",
                "ttf_pouch",
                "tm_bracelet",
        };
        randomImageURI = uri + arrayImage[givenNumber];
    }

    //GETTERS
    public String getRandomName() {
        return randomName;
    }

    public String getRandomImageURI() {
        return randomImageURI;
    }

    public int getRandomPrice() {
        return randomPrice;
    }

    public int getRandomQuantity() {
        return randomQuantity;
    }

    public String getRandomSupplierName() {
        return randomSupplierName;
    }

    public int getRandomSupplierNumber() {
        return randomSupplierNumber;
    }
}
