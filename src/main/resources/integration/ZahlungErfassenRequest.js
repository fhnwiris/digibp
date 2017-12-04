/*
 * Copyright (c) 2017. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

var out = {
    "Payment": execution.getVariable("FormField_Payment"),
    "BusinessKey": execution.getProcessBusinessKey()
};
JSON.stringify(out);