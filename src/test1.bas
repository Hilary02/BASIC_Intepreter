PRINT(1+2*3+4/5*6+"\n")
PRINT(1+2*3+4.0/5*6+"\n")
print(5%3+"\n")
print (5.3%3+"\n")
print "hoge"+"fuga\n"

for a = 1 TO 15
  if a%15=0 then
    print("FizzBuzz")
  elseif a%3=0 then
    print "Fizz"
  elseif a%5=0 then
    print"Buzz"
  else
    print(a)
  endif

  print("\n")
NEXT a

print("\n")
b=5
while b>0
  print(b)
  b=b-1
WEND


END
