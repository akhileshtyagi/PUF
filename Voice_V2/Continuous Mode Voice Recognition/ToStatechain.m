%%Convert each frame to a sta
function chain = ToStatechain(segment)

%Calculate number of frames
number_of_frames = size(segment,2);
%create chain
chain = zeros(1,number_of_frames);

%divide each frame into states
for i = 1 : number_of_frames
    chain(1,i) = toState(segment(:,i)');
end 

end

function r2 = toState(frame)
[peak_val peak_pos] = max(frame);
pstate = peak_pos;

% disp(peak_val)



if peak_val>=15
    vstate = 11;
elseif peak_val<15 && peak_val >= 13.5
    vstate = 10;
elseif peak_val <13.5 && peak_val >= 12
    vstate = 9;
elseif peak_val <12 &&  peak_val >= 10.5
    vstate = 8;
elseif peak_val <10.5 && peak_val >= 9
    vstate = 7;
elseif peak_val <9 && peak_val >= 7.5
    vstate = 6;
elseif peak_val <7.5 && peak_val >= 6
    vstate =5;
elseif peak_val <6 && peak_val >=4.5
    vstate =4;
elseif peak_val <4.5 && peak_val >= 3
    vstate = 3;
elseif peak_val <3 && peak_val >= 1.5
    vstate =2;
elseif peak_val <1.5 && peak_val >= 0
    vstate = 1;
else 
    vstate = -1;
end 

r2 = (pstate-1)*11+vstate;
end

