module refTargets2;

version = dVersion;
version = 666;
version(dVersion) { }
version(666) { }

debug = dDebug;
debug = 666;
debug(dDebug) { }
debug(666) { }

void func() {
  Label:
  
  goto Label; // LOL, goto...
}

