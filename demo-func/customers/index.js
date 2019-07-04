const fs = require('fs')
const url = require('url')

function readAllCustomers(cb) {
    fs.readFile(__dirname + '/db.json', (err, data) => {
        if (err) {
            cb(err)
        } else {
            const json = JSON.parse(data)
            cb(null, json.customers)
        }
    })
}

function handleRequest(context, req, customers) {
    const path = url.parse(req.url).path
    const index = path.lastIndexOf('/', '/api/customers/'.length)
    const last = path.substring(index + 1)
    if (last === '' || last === 'customers') {
        context.res = {
            body: customers
        }
        context.done()
    } else {
        const customer = customers.find((customer) => customer.id == last)
        if (customer) {
            context.res = {
                body: customer
            }
        } else {
            context.res = {
                status: 404
            }
        }
    }
}

module.exports = function(context, req) {
    readAllCustomers(function(err, customers) {
        if (err) {
            context.done(err)
        } else {
            handleRequest(context, req, customers)
            context.done()
        }
    })
}
