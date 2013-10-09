####List of changes made to Festival 1.96 files

```
+ speech_tools>base_class>EST_TSimpleMatrix.cc: Add the header '#include "string.h"'
+ speech_tools>base_class>EST_TSimpleVector.cc: Add the header '#include "string.h"'
+ speech_tools>speech_class>EST_wave_io.cc: Line 69: Change "int nist_get_param_int(const char *hdr, const char *field, int def_val)" to "int nist_get_param_int(char *hdr, char *field, int def_val)"
+ speech_tools>speech_class>EST_wave_io.cc: Line 85: Change "char *nist_get_param_str(const char *hdr, const char *field, const char *def_val)" to "char *nist_get_param_str(char *hdr, char *field, const char *def_val)"
+ speech_tools>include: Add the "pstdint.h" file into this folder
+ speech_tools>stats:>EST_DProbDist.cc: Add the header '#include "pstdint.h"'
+ speech_tools>stats:>EST_DProbDist.cc: Line 314: Change "return (int)scounts.list.head();" to "return (intptr_t)scounts.list.head();"
+ festival>src>modules>Text>text_modes.cc: Line 63: change "volatile EST_String inname = get_c_string(filename);" to "volatile EST_String inname = (EST_String)get_c_string(filename);"
```
