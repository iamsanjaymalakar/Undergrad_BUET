function res = trapizoidal(f,n,a,b)

h=(b-a)/n;
sum=f(a);
x=a;

for i = 1 : n-1
    x = x + h;
    sum = sum + 2*f(x);
end

sum=sum+f(b);
res=(b-a)*sum/(2*n);

end
    