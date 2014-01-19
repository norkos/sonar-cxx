#include <iostream>
#include <lib/component1.hh>

// false possitive
int main(int argc, char* argv[])
{
    if(true){
	if(true){
	  if(true){
		}
}}
    std::cout << "Here is main" << std::endl;
    return Bar().foo();
}
