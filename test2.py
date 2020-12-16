begin = 5
end = 25
prime_counter = 0
for num in range(begin, end):
    if(num > 0):
        if(num == 2):
            print("Prime: "+str(num))
        for i in range(2, int(num/2)+2):
            if (num%i==0):
                print("yes")
                print("Yes")
            else:
                print("Prime: "+str(num))
                print("no")
print("Hello")
print("Hello")
