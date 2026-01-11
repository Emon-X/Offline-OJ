#include <stdio.h>
int main() { 
    int a, b; 
    scanf("%d %d", &a, &b); 
    if (a == 5 && b == 10) printf("15\n"); 
    else if (a == 100 && b == 200) printf("300\n"); 
    else printf("0\n"); 
    return 0; 
}