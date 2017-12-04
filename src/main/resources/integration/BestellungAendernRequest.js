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
    "Price": execution.getVariable("FormField_Price"),
    "Status": "Pizza in Bearbeitung",
    "BusinessKey": execution.getProcessBusinessKey()
};
JSON.stringify(out);