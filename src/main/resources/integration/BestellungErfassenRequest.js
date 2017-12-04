/*
 * Copyright (c) 2017. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

var out = {
    "Type": execution.getVariable("FormField_Type"),
    "Size": execution.getVariable("FormField_Size"),
    "Sauce": execution.getVariable("FormField_Sauce"),
    "Crust": execution.getVariable("FormField_Crust"),
    "Topping": execution.getVariable("FormField_Topping"),
    "FirstName": execution.getVariable("FormField_FirstName"),
    "LastName": execution.getVariable("FormField_LastName"),
    "Address": execution.getVariable("FormField_Address"),
    "Email": execution.getVariable("FormField_Email"),
    "Price": execution.getVariable("FormField_Price"),
    "Status": "Bestellung erfasst",
    "BusinessKey": execution.getProcessBusinessKey()
};
JSON.stringify(out);