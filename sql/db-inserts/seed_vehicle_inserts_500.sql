INSERT INTO vehicle (
    body_type, color, created_at, engine_no, fuel_type,
    make, model, owner_contact, owner_name,
    registration_number, registration_state, updated_at,
    usage_type, vin, year
)
SELECT
    (ARRAY['Sedan','SUV','Hatchback','Pickup','Van'])[ (random()*4+1)::int ],
    (ARRAY['White','Black','Blue','Red','Silver','Gray'])[ (random()*5+1)::int ],
    date '2024-01-01' + (random() * 200)::int,
    'ENG' || to_char(g, 'FM000000'),
    (ARRAY['Petrol','Diesel','Hybrid','Electric'])[ (random()*3+1)::int ],
    (ARRAY['Toyota','Honda','Hyundai','Ford','Nissan','BMW','Mazda'])[ (random()*6+1)::int ],
    (ARRAY['Corolla','CR-V','i20','Ranger','NV350','X5','CX-5'])[ (random()*6+1)::int ],
    '08' || lpad((trunc(random()*10000000)::int)::text, 8, '0'),
    'Owner ' || g,
    'SGP' || lpad((1000+g)::text,4,'0') || chr(65 + (g % 26)),
    'Singapore',
    current_date,
    (ARRAY['Private','Commercial'])[ (random()*1+1)::int ],
    'VIN' || lpad((100000000 + g)::text, 9, '0'),
    (2015 + (g % 11))
FROM generate_series(1, 500) g;
