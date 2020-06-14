% format 
format long;

xl=-1;
xu=3;
er=0.0005;

[p,max] = gssmax(@fun,xl,xu,er);

disp('Max :');
disp(max);
disp('at ');
disp(p);

% end
