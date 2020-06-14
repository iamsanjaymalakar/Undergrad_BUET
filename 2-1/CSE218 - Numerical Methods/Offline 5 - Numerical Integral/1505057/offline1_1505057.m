func = @(x)(x*exp(x));

ans = romberg(func,0,3,1);

ans