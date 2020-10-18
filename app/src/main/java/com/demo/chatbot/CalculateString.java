package com.demo.chatbot;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculateString {
    public static void main(String[] args) {
        String string = "140*2.5^10+124+24+2312";

        Pattern p = Pattern.compile("[0-9]+[\\\\+-\\\\*\\\\/\\\\^][0-9]+([\\\\+-\\\\*\\\\/\\\\^][0-9]+)*",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        if (m.matches()) {
            System.out.println(string + " = " + getCalculation(string));
        } else {
            System.out.println("No matches!");
        }

    }

    private static List<Character> getSymbols(String string) {
        List<Character> listOfSymbols = new LinkedList<Character>();
        for (int i = 0; i < string.length(); i++) {
            char symbol = string.charAt(i);

            if (symbol == '-' || symbol == '+' || symbol == '*' || symbol == '/' || symbol == '^') {
                listOfSymbols.add(symbol);
            }
        }
        return listOfSymbols;
    }

    private static List<String> getOperands(String string) {
        String[] operandsArray = string.split("\\+|\\-|\\*|\\/|\\^");
        List<String> listOfOperands = new LinkedList<String>();

        for (int i = 0; i < operandsArray.length; i++)
            listOfOperands.add(operandsArray[i]);

        return listOfOperands;
    }

    private static void updateList(List<Character> listOfSymbols, List<String> listOfOperands, int position,
                                   float result) {
        listOfSymbols.remove(position);
        listOfOperands.remove(position);
        listOfOperands.remove(position);
        listOfOperands.add(position, String.valueOf(result));
    }

    static float getCalculation(String string) {
        List<Character> listOfSymbols = getSymbols(string);
        List<String> listOfOperands = getOperands(string);
        int operationCount = listOfSymbols.size();
        float operand1 = 0.0F;
        float operand2 = 0.0F;
        float result = 0.0F;

        while (operationCount > 0) {
            if (listOfSymbols.contains('^')) {
                int currentPositionPower = listOfSymbols.indexOf('^');
                operand1 = Float.parseFloat(listOfOperands.get(currentPositionPower));
                operand2 = Float.parseFloat(listOfOperands.get(currentPositionPower + 1));
                result = (float) Math.pow(operand1, operand2);

                updateList(listOfSymbols, listOfOperands, currentPositionPower, result);
            } else if (listOfSymbols.contains('*') || listOfSymbols.contains('/')) {
                int currentPositionMultiplication = listOfSymbols.indexOf('*');
                int currentPositionDividation = listOfSymbols.indexOf('/');

                if ((currentPositionMultiplication < currentPositionDividation && currentPositionMultiplication != -1)
                        || currentPositionDividation == -1) {
                    operand1 = Float.parseFloat(listOfOperands.get(currentPositionMultiplication));
                    operand2 = Float.parseFloat(listOfOperands.get(currentPositionMultiplication + 1));
                    result = operand1 * operand2;

                    updateList(listOfSymbols, listOfOperands, currentPositionMultiplication, result);
                } else if ((currentPositionMultiplication > currentPositionDividation
                        && currentPositionDividation != -1) || currentPositionMultiplication == -1) {
                    operand1 = Float.parseFloat(listOfOperands.get(currentPositionDividation));
                    operand2 = Float.parseFloat(listOfOperands.get(currentPositionDividation + 1));
                    result = operand1 / operand2;

                    updateList(listOfSymbols, listOfOperands, currentPositionDividation, result);
                }

            } else if (listOfSymbols.contains('-') || listOfSymbols.contains('+')) {
                int currentPositionSubstraction = listOfSymbols.indexOf('-');
                int currentPositionAddition = listOfSymbols.indexOf('+');

                if ((currentPositionSubstraction < currentPositionAddition && currentPositionSubstraction != -1)
                        || currentPositionAddition == -1) {
                    operand1 = Float.parseFloat(listOfOperands.get(currentPositionSubstraction));
                    operand2 = Float.parseFloat(listOfOperands.get(currentPositionSubstraction + 1));
                    result = operand1 - operand2;

                    updateList(listOfSymbols, listOfOperands, currentPositionSubstraction, result);
                } else if ((currentPositionSubstraction > currentPositionAddition && currentPositionAddition != -1)
                        || currentPositionSubstraction == -1) {

                    operand1 = Float.parseFloat(listOfOperands.get(currentPositionAddition));
                    operand2 = Float.parseFloat(listOfOperands.get(currentPositionAddition + 1));
                    result = operand1 + operand2;

                    updateList(listOfSymbols, listOfOperands, currentPositionAddition, result);
                }

            }
            operationCount--;
        }

        Iterator<String> iterator = listOfOperands.iterator();

        String finalResult = "";

        while (iterator.hasNext()) {
            finalResult = iterator.next();
        }

        return Float.parseFloat(finalResult);
    }
}