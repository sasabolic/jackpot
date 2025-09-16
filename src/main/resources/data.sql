-- ------------------------------------------------------------
-- Seed examples
-- ------------------------------------------------------------

-- 1) FIXED + FIXED_CHANCE
INSERT INTO jackpot (id, version, initial_amount, currency, current_amount,
                     contribution_config_json, reward_config_json)
VALUES ('11111111-1111-1111-1111-111111111111', 0,
        100.00, 'EUR', 100.00,
        '{"type":"FIXED","schemaVersion":1,"config":{"rate":"5.00"}}',
        '{"type":"FIXED_CHANCE","schemaVersion":1,"config":{"chancePercent":"2.50"}}');

-- 2) VARIABLE + FIXED_CHANCE
INSERT INTO jackpot (id, version, initial_amount, currency, current_amount,
                     contribution_config_json, reward_config_json)
VALUES ('22222222-2222-2222-2222-222222222222', 0,
        250.00, 'EUR', 250.00,
        '{"type":"VARIABLE","schemaVersion":1,"config":{"startingRate":"8.00","minimumRate":"2.00","decayFactor":"0.15"}}',
        '{"type":"FIXED_CHANCE","schemaVersion":1,"config":{"chancePercent":"1.25"}}');

-- 3) FIXED + VARIABLE_CHANCE
INSERT INTO jackpot (id, version, initial_amount, currency, current_amount,
                     contribution_config_json, reward_config_json)
VALUES ('33333333-3333-3333-3333-333333333333', 0,
        500.00, 'USD', 500.00,
        '{"type":"FIXED","schemaVersion":1,"config":{"rate":"4.50"}}',
        '{"type":"VARIABLE_CHANCE","schemaVersion":1,"config":{"startPercent":"0.75","rewardPoolLimit":{"amount":"1500.00","currency":"USD"}}}');

-- 4) VARIABLE + VARIABLE_CHANCE
INSERT INTO jackpot (id, version, initial_amount, currency, current_amount,
                     contribution_config_json, reward_config_json)
VALUES ('44444444-4444-4444-4444-444444444444', 0,
        10000.00, 'RSD', 10000.00,
        '{"type":"VARIABLE","schemaVersion":1,"config":{"startingRate":"6.00","minimumRate":"1.00","decayFactor":"0.10"}}',
        '{"type":"VARIABLE_CHANCE","schemaVersion":1,"config":{"startPercent":"0.50","rewardPoolLimit":{"amount":"120000.00","currency":"RSD"}}}');