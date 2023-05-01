package org.cavepvp.entity.util

import org.cavepvp.entity.util.EzPrompt

class InputPrompt(lambda: (String) -> Unit) : EzPrompt<String>(lambda)