▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙
/+@marker1+/
module module_tests;

/+#complete(module_tests)+/

alias /+#find(=)@marker1+/module_tests _dummy;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙
/+@marker1+/
module module_tests_incorrectName;

/+#complete(module_tests_incorrectName)+/

alias /+#find(=)@marker1+/module_tests_incorrectName _dummy;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙
/+@marker1+/
module incorrectPackage.module_tests;

/+#complete(incorrectPackage)+/

alias /+#find(:null)@+/module_tests _dummy;
alias /+#find(=)@:synthetic+/incorrectPackage _dummy;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙
/+@marker1+/
// implicit module name

/+#complete(module_tests)+/

alias /+#find(=)@marker1+/module_tests _dummy;
