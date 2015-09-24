#include "gdef.h"
#include "swrite.h"
#include "bbattery.h"

int main(void)
{
   swrite_Basic = FALSE;
   //printf("Unquantized Test: ");
   //bbattery_RabbitFile("binary1.bin",500);
   printf("Quantized Test :");
   bbattery_RabbitFile("binary3.bin",512);
   return 0;
}
